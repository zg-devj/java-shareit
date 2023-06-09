package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentNewDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    // Сохранение вещи
    @Override
    @Transactional
    public ItemDto saveItem(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь c id=%d не найдена.", userId)));

        Item item = ItemMapper.dtoToItem(itemDto);
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException(
                            String.format("Запрос вещи c id=%d не найдена.", itemDto.getRequestId())));
            item.setRequest(itemRequest);
        }
        item.setOwner(user);

        Item saved = itemRepository.save(item);
        log.info("Пользователь id={} добавил вещь с id={}.", userId, saved.getId());
        return ItemMapper.itemToDto(saved);
    }

    // Обновление вещи
    @Override
    @Transactional
    public ItemDto updateItem(Long userId, ItemDto itemDto) {

        Item updated = itemRepository.findById(itemDto.getId())
                .orElseThrow(() -> new NotFoundException(String.format("Вещь c id=%d не найдена.", itemDto.getId())));

        User user = updated.getOwner();

        if (!Objects.equals(user.getId(), userId)) {
            // Вещь не является вещью пользователя
            throw new ForbiddenException("У вас нет прав для редактирования.");
        }
        if (itemDto.getName() != null) {
            log.info("Обновляется имя вещи с id={}.", updated.getId());
            updated.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            log.info("Обновляется описание вещи с id={}.", updated.getId());
            updated.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            log.info("Обновляется статус одобрения вещи с id={}.", updated.getId());
            updated.setAvailable(itemDto.getAvailable());
        }
        log.info("Пользователь id={} обновил вещь с id={}.", userId, updated.getId());
        return ItemMapper.itemToDto(itemRepository.save(updated));
    }

    // Получение последнего бронирования для вещи
    private BookingShort getLastBooking(Long itemId, Long userId) {
        PageRequest page = PageRequest.of(0, 1);
        LocalDateTime now = LocalDateTime.now();
        Page<BookingShort> last = bookingRepository.getLastBooking(itemId, userId,
                BookingStatus.APPROVED, now, page);
        if (last != null) {
            return last.get().findFirst().orElse(null);
        }
        return null;
    }

    // Получение следующего бронирования для вещи
    private BookingShort getNextBooking(Long itemId, Long userId) {
        PageRequest page = PageRequest.of(0, 1);
        LocalDateTime now = LocalDateTime.now();
        Page<BookingShort> next = bookingRepository.getNextBooking(itemId, userId,
                BookingStatus.APPROVED, now, page);
        if (next != null) {
            return next.get().findFirst().orElse(null);
        }
        return null;
    }

    // Вернуть вещи с комментариями
    @Override
    public ItemBookingDto findById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Вещь c id=%d не найдена.", itemId)));
        List<Comment> comments = commentRepository.findCommentsByItemIdOrderByCreatedAsc(item.getId());
        return ItemMapper.toItemBookingDto(item,
                getLastBooking(item.getId(), userId),
                getNextBooking(item.getId(), userId),
                comments);
    }

    // Вернуть вещи владельца с комментариями
    @Override
    public List<ItemBookingDto> findAllByUserId(Long userId, int from, int size) {
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<ItemBookingDto> returned = new ArrayList<>();
        List<Item> items = itemRepository.findAllByOwnerIdOrderByIdAsc(userId, pageRequest);
        List<Comment> comments = commentRepository.findCommentsByItemIsIn(items);
        for (Item item : items) {
            List<Comment> comments1 = comments.stream()
                    .filter(c -> Objects.equals(c.getItem().getId(), item.getId()))
                    .sorted(Comparator.comparing(Comment::getCreated))
                    .collect(Collectors.toList());
            BookingShort last = getLastBooking(item.getId(), userId);
            BookingShort next = getNextBooking(item.getId(), userId);
            returned.add(ItemMapper.toItemBookingDto(item, last, next, comments1));
        }
        return returned;
    }

    // Поиск по названия или описанию
    @Override
    public List<ItemDto> search(String search, int from, int size) {
        if (search != null && search.isBlank()) {
            return new ArrayList<>();
        }
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        return ItemMapper.itemToDto(
                itemRepository.search(search, pageRequest));
    }

    // Добавить комментарий для бронирования
    // если пользователь брал в пользование вещью
    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentNewDto comment) {
        if (comment.getText() != null && comment.getText().isEmpty()) {
            throw new BadRequestException("Текст комментария не должен быть пустым.");
        }
        LocalDateTime now = LocalDateTime.now();
        PageRequest pageRequest = PageRequest.of(0, 1);
        Page<Booking> page =
                bookingRepository.findBookingForComment(userId, itemId,
                        now, BookingStatus.APPROVED, pageRequest);

        Booking booking = page.get().findFirst().orElseThrow(
                () -> new BadRequestException("Вы не можете оставить комментарий к бронированию."));

        Comment commentNew = CommentMapper.dtoToComment(booking.getBooker(), booking.getItem(), comment);
        Comment saved = commentRepository.save(commentNew);
        return CommentMapper.commentToDto(saved);
    }
}
