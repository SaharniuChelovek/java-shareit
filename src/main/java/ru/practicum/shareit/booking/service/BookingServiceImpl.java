package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.exception.ForbiddenException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemDbRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserDbRepository;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemDbRepository itemRepository;
    private final UserDbRepository userRepository;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              ItemDbRepository itemRepository,
                              UserDbRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BookingDto createBooking(Long bookerId, CreateBookingDto createBookingDto) {

        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(createBookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!item.isAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }

        Booking booking = BookingMapper.toBooking(createBookingDto);
        booking.setBooker(booker);
        booking.setItem(item);

        Booking savedBooking = bookingRepository.save(booking);

        return BookingMapper.toBookingDto(savedBooking);
    }

    @Override
    public BookingDto approveBooking(Long userId, Long bookingId, boolean approved) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Подтвердить бронирование может только владелец вещи");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        Booking updatedBooking = bookingRepository.save(booking);

        return BookingMapper.toBookingDto(updatedBooking);
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {

        Booking booking = bookingRepository
                .findByIdAndBookerIdOrIdAndItemOwnerId(bookingId, userId, bookingId, userId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookings(Long bookerId, BookingState state) {

        if (userRepository.findById(bookerId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }

        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state) {
            case ALL ->
                    bookingRepository.findAllByBookerIdOrderByStartDateDesc(bookerId);
            case CURRENT ->
                    bookingRepository.findAllByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(bookerId, now, now);
            case PAST ->
                    bookingRepository.findAllByBookerIdAndEndDateBeforeOrderByStartDateDesc(bookerId, now);
            case FUTURE ->
                    bookingRepository.findAllByBookerIdAndStartDateAfterOrderByStartDateDesc(bookerId, now);
            case WAITING ->
                    bookingRepository.findAllByBookerIdAndStatusOrderByStartDateDesc(bookerId, BookingStatus.WAITING);
            case REJECTED ->
                    bookingRepository.findAllByBookerIdAndStatusOrderByStartDateDesc(bookerId, BookingStatus.REJECTED);
        };

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long ownerId, BookingState state) {

        if (userRepository.findById(ownerId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }

        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state) {
            case ALL ->
                    bookingRepository.findAllByItemOwnerIdOrderByStartDateDesc(ownerId);
            case CURRENT ->
                    bookingRepository.findAllByItemOwnerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(ownerId, now, now);
            case PAST ->
                    bookingRepository.findAllByItemOwnerIdAndEndDateBeforeOrderByStartDateDesc(ownerId, now);
            case FUTURE ->
                    bookingRepository.findAllByItemOwnerIdAndStartDateAfterOrderByStartDateDesc(ownerId, now);
            case WAITING ->
                    bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDateDesc(ownerId, BookingStatus.WAITING);
            case REJECTED ->
                    bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDateDesc(ownerId, BookingStatus.REJECTED);
        };

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }
}
