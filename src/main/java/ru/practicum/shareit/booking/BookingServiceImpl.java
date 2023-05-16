package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
//@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    //@Transactional
    public BookingDto createBooking(Long userId, BookingNewDto bookingNewDto) {
        validDateForBookingNewDto(bookingNewDto);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь c id=%d не найден.", userId)));

        Item item = itemRepository.findById(bookingNewDto.getItemId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Вещь c id=%d не найдена.", bookingNewDto.getItemId())));
        if (!item.getAvailable()) {
            throw new BadRequestException(
                    String.format("Вещь c id=%d не доступна для бронирования.", bookingNewDto.getItemId()));
        }
        Booking booking = BookingMapper.toBooking(bookingNewDto, item, user);
        Booking saved = bookingRepository.save(booking);
        BookingDto ret = BookingMapper.toBookingDto(saved);
        return ret;
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
