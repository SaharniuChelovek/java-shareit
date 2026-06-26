package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.UpdateBookingDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

public class BookingMapper {

    public static Booking toBooking(CreateBookingDto dto) {
        Booking booking = new Booking();
        booking.setStartDate(dto.getStartDate());
        booking.setEndDate(dto.getEndDate());
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    public static void updateBookingFromDto(UpdateBookingDto dto, Booking booking) {
        if (dto.getStatus() != null) {
            booking.setStatus(dto.getStatus());
        }
    }

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getItem() != null ? ItemMapper.toItemDto(booking.getItem()) : null,
                booking.getBooker() != null ? UserMapper.toUserDto(booking.getBooker()) : null,
                booking.getStatus()
        );
    }
}
