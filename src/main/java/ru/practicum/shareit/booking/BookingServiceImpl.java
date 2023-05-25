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
import java.util.EnumSet;
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

    // Создание бронирования
    @Override
    @Transactional
    public BookingDto createBooking(Long userId, BookingNewDto bookingNewDto) {
        validDateForBookingNewDto(bookingNewDto);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь c id=%d не найден.", userId)));

        Item item = itemRepository.findByIdAndOwnerNot(bookingNewDto.getItemId(), user)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Вещь c id=%d не найдена.", bookingNewDto.getItemId())));
        if (!item.getAvailable()) {
            throw new BadRequestException(
                    String.format("Вещь c id=%d не доступна для бронирования.", bookingNewDto.getItemId()));
        }
        Booking booking = BookingMapper.dtoToBooking(bookingNewDto, item, user);
        Booking saved = bookingRepository.save(booking);
        return BookingMapper.bookingToDto(saved);
    }

    // Одобрение или отказ бронирования владельцем
    @Override
    @Transactional
    public BookingDto approve(Long userId, boolean approve, Long bookingId) {
        Booking booking = bookingRepository.findBookingForApprove(bookingId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Бронирование c id=%d не найден.", bookingId)));
        if (approve && booking.getStatus() == BookingStatus.APPROVED) {
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
        return BookingMapper.bookingToDto(bookingRepository.save(booking));
    }

    // Вернуть бронирование для бронирующего или владельца
    @Override
    public BookingDto getBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findBookingByOwnerOrBooker(bookingId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Бронирование c id=%d не найден.", bookingId)));
        return BookingMapper.bookingToDto(booking);
    }

    // Вернуть все бронирования веши бронирующего
    @Override
    public List<BookingDto> getAllBookings(Long userId, State state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь c id=%d не найден.", userId));
        }
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case WAITING:
                return BookingMapper.bookingToDto(bookingRepository
                        .findAllByBookerIdAndStatusEqualsOrderByStartDesc(userId,
                                BookingStatus.WAITING));
            case CURRENT:
                return BookingMapper.bookingToDto(bookingRepository
                        .findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                                now, now));
            case REJECTED:
                Set<BookingStatus> statusSet = EnumSet.of(BookingStatus.REJECTED,
                        BookingStatus.CANCELED);
                return BookingMapper.bookingToDto(bookingRepository
                        .findAllByBookerIdAndStatusInOrderByStartDesc(userId, statusSet));
            case PAST:
                return BookingMapper.bookingToDto(bookingRepository
                        .findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now));
            case FUTURE:
                return BookingMapper.bookingToDto(bookingRepository
                        .findAllByBookerIdAndStartAfterOrderByStartDesc(userId, now));
            case ALL:
            default:
                return BookingMapper.bookingToDto(bookingRepository.findAllByBookerIdOrderByStartDesc(userId));
        }
    }

    // Вернуть все бронирования веши для владельца
    @Override
    public List<BookingDto> getAllBookingsForOwner(Long userId, State state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь c id=%d не найден.", userId));
        }
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case WAITING:
                return BookingMapper.bookingToDto(bookingRepository
                        .findAllByItemOwnerIdAndStatusEqualsOrderByStartDesc(userId, BookingStatus.WAITING));
            case CURRENT:
                return BookingMapper.bookingToDto(bookingRepository
                        .findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                                now, now));
            case REJECTED:
                Set<BookingStatus> statusSet = EnumSet.of(BookingStatus.REJECTED,
                        BookingStatus.CANCELED);
                return BookingMapper.bookingToDto(bookingRepository
                        .findAllByItemOwnerIdAndStatusInOrderByStartDesc(userId, statusSet));
            case PAST:
                return BookingMapper.bookingToDto(bookingRepository
                        .findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, now));
            case FUTURE:
                return BookingMapper.bookingToDto(bookingRepository
                        .findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, now));
            case ALL:
            default:
                return BookingMapper.bookingToDto(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId));
        }
    }

    // Проверка бронирования
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
