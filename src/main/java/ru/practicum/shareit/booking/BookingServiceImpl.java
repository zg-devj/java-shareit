package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingDto createBooking(Long userId, BookingNewDto bookingNewDto) {
        validDateForBookingNewDto(bookingNewDto);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь c id=%d не найден.", userId)));

        //Item item = itemRepository.findById(bookingNewDto.getItemId())
        Item item = itemRepository.findByIdAndOwnerNot(bookingNewDto.getItemId(), user)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Вещь c id=%d не найдена.", bookingNewDto.getItemId())));
        if (!item.getAvailable()) {
            throw new BadRequestException(
                    String.format("Вещь c id=%d не доступна для бронирования.", bookingNewDto.getItemId()));
        }
        Booking booking = BookingMapper.toBooking(bookingNewDto, item, user);
        Booking saved = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(saved);
    }

    @Override
    @Transactional
    public BookingDto approve(Long userId, boolean approve, Long bookingId) {
        Booking booking = bookingRepository.findBookingForApprove(bookingId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Бронирование c id=%d не найден.", bookingId)));
        if (approve && booking.getStatus() == BookingStatus.APPROVED)
        //        ||(!approve && booking.getStatus() != BookingStatus.APPROVED))
        {
            throw new BadRequestException("Бронирование уже имеет устанавливаемый статус.");
        }
        User owner = booking.getItem().getOwner();
        if (!Objects.equals(userId, owner.getId())) {
            throw new ForbiddenException("У вас нет прав для изменения статуса бронирования.");
        }
        if (approve) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findBookingByOwnerOrBooker(bookingId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Бронирование c id=%d не найден.", bookingId)));
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookings(Long userId, State state, boolean isOwner) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь c id=%d не найден.", userId));
        }
        switch (state) {
            case WAITING:
                if (isOwner) {
                    List<Booking> list = bookingRepository
                            .findAllByItem_OwnerIdAndStatusEqualsOrderByStartDesc(userId, BookingStatus.WAITING);
                    return BookingMapper.toBookingDto(list);
                } else {
                    List<Booking> list = bookingRepository
                            .findAllByBooker_IdAndStatusEqualsOrderByStartDesc(userId, BookingStatus.WAITING);
                    return BookingMapper.toBookingDto(list);
                }
            case CURRENT:
                LocalDateTime now = LocalDateTime.now();
                if (isOwner) {
                    List<Booking> list = bookingRepository
                            .findAllByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
                    return BookingMapper.toBookingDto(list);
                } else {
                    List<Booking> list = bookingRepository
                            .findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
                    return BookingMapper.toBookingDto(list);
                }
            case REJECTED:
                Set<BookingStatus> statusSet = new HashSet<>();
                statusSet.add(BookingStatus.REJECTED);
                statusSet.add(BookingStatus.CANCELED);
                if (isOwner) {
                    List<Booking> list = bookingRepository
                            .findAllByItem_OwnerIdAndStatusInOrderByStartDesc(userId, statusSet);
                    return BookingMapper.toBookingDto(list);
                } else {
                    List<Booking> list = bookingRepository
                            .findAllByBooker_IdAndStatusInOrderByStartDesc(userId, statusSet);
                    return BookingMapper.toBookingDto(list);
                }
            case PAST:
                if (isOwner) {
                    List<Booking> list = bookingRepository
                            .findAllByItem_OwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                    return BookingMapper.toBookingDto(list);
                } else {
                    List<Booking> list = bookingRepository
                            .findAllByBooker_IdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                    return BookingMapper.toBookingDto(list);
                }
            case FUTURE:
                if (isOwner) {
                    List<Booking> list = bookingRepository
                            .findAllByItem_OwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                    return BookingMapper.toBookingDto(list);
                } else {
                    List<Booking> list = bookingRepository
                            .findAllByBooker_IdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                    return BookingMapper.toBookingDto(list);
                }
            case ALL:
            default:
                if (isOwner) {
                    List<Booking> list = bookingRepository.findAllByItem_OwnerIdOrderByStartDesc(userId);
                    return BookingMapper.toBookingDto(list);
                } else {
                    List<Booking> list = bookingRepository.findAllByBooker_IdOrderByStartDesc(userId);
                    return BookingMapper.toBookingDto(list);
                }
        }
    }

    private void validDateForBookingNewDto(BookingNewDto bookingNewDto) {
        if (bookingNewDto.getStart() == null) {
            throw new BadRequestException("Дата начала бронирования должны быть указано.");
        }
        if (bookingNewDto.getEnd() == null) {
            throw new BadRequestException("Дата окончания бронирования должны быть указано.");
        }
        if (bookingNewDto.getStart().isEqual(bookingNewDto.getEnd())) {
            throw new BadRequestException("Дата начала бронирования не должна совпадать " +
                    "с датой окончания бронирования.");
        }
        if (bookingNewDto.getStart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Дата начала бронирования не должна быть " +
                    "в прошлом.");
        }
        if (bookingNewDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Дата окончания бронирования не должна быть " +
                    "в прошлом.");
        }
        if (bookingNewDto.getStart().isAfter(bookingNewDto.getEnd())) {
            throw new BadRequestException("Дата начала бронирования не должна быть " +
                    "позже даты окончания бронирования.");
        }
    }
}
