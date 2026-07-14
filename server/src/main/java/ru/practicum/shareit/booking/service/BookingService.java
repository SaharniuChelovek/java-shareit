package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.enums.BookingState;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(Long bookerId, CreateBookingDto createBookingDto);

    BookingDto approveBooking(Long userId, Long bookingId, boolean approved);

    BookingDto getBookingById(Long userId, Long bookingId);

    List<BookingDto> getBookings(Long bookerId, BookingState state);

    List<BookingDto> getOwnerBookings(Long ownerId, BookingState state);

}
