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
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto saveItem(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь c id=%d не найдена", userId)));
        Item item = ItemMapper.dtoToItem(itemDto);
        item.setOwner(user);
        Item saved = itemRepository.save(item);
        log.info("Пользователь id={} добавил вещь с id={}", userId, saved.getId());
        return ItemMapper.itemToDto(saved);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, ItemDto itemDto) {

        Item updated = itemRepository.findById(itemDto.getId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Вещь c id=%d не найдена", itemDto.getId())));

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
        log.info("Пользователь id={} обновил вещь с id={}", userId, updated.getId());
        return ItemMapper.itemToDto(itemRepository.save(updated));
    }

    private Set<BookingStatus> getIn() {
        return new HashSet<>(List.of(BookingStatus.APPROVED));
    }

    private BookingShort getLastBooking(Long itemId, Long userId) {
        PageRequest page = PageRequest.of(0, 1);
        LocalDateTime now = LocalDateTime.now();
        List<BookingShort> lastList = bookingRepository.getLastBooking(itemId, userId,
                getIn(), now, page);
        if (lastList.size() > 0) {
            return lastList.get(0);
        }
        return null;
    }

    private BookingShort getNextBooking(Long itemId, Long userId) {
        PageRequest page = PageRequest.of(0, 1);
        LocalDateTime now = LocalDateTime.now();
        List<BookingShort> nextList = bookingRepository.getNextBooking(itemId, userId,
                getIn(), now, page);
        if (nextList.size() > 0) {
            return nextList.get(0);
        }
        return null;
    }

    @Override
    public ItemBookingDto findById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Вещь c id=%d не найдена", itemId)));
        List<Comment> comments = commentRepository.findCommentsByItemIdOrderByCreatedAsc(item.getId());
        return ItemMapper.toItemBookingDto(item,
                getLastBooking(item.getId(), userId),
                getNextBooking(item.getId(), userId),
                comments);
    }

    @Override
    public List<ItemBookingDto> findAllByUserId(Long userId) {
        List<ItemBookingDto> returned = new ArrayList<>();
        List<Item> items = itemRepository.findAllByOwnerIdOrderByIdAsc(userId);
        for (Item item : items) {
            List<Comment> comments = commentRepository.findCommentsByItemIdOrderByCreatedAsc(item.getId());
            returned.add(ItemMapper.toItemBookingDto(item,
                    getLastBooking(item.getId(), userId),
                    getNextBooking(item.getId(), userId),
                    comments));
        }
        return returned;
    }

    @Override
    public List<ItemDto> search(String search) {
        if (search != null && search.isBlank()) {
            return new ArrayList<>();
        }
        return ItemMapper.itemToDto(
                itemRepository.search(search));
    }

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
                () -> new BadRequestException("Не верный запрос на комментарий"));

        Comment commentNew = Comment.builder()
                .author(booking.getBooker())
                .text(comment.getText())
                .item(booking.getItem())
                .created(LocalDateTime.now())
                .build();
        Comment saved = commentRepository.save(commentNew);
        return CommentMapper.commentToDto(saved);
    }
}
