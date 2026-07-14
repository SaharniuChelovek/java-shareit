package ru.practicum.shareit.mappertest;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.UpdateBookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BookingMapperTest {

    @Test
    void toBooking_shouldMapCorrectly() {
        CreateBookingDto dto = new CreateBookingDto(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3),
                1L
        );

        Booking booking = BookingMapper.toBooking(dto);

        assertNull(booking.getId());
        assertEquals(dto.getStartDate(), booking.getStartDate());
        assertEquals(dto.getEndDate(), booking.getEndDate());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
        assertNull(booking.getItem());
        assertNull(booking.getBooker());
    }

    @Test
    void toBookingDto_shouldMapCorrectly() {
        User owner = new User(1L, "Owner", "owner@mail.com");
        User booker = new User(2L, "Booker", "booker@mail.com");
        Item item = new Item(1L, "Дрель", "Мощная дрель", true, owner, null);
        Booking booking = new Booking(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3),
                item,
                booker,
                BookingStatus.WAITING
        );

        BookingDto dto = BookingMapper.toBookingDto(booking);

        assertEquals(1L, dto.getId());
        assertEquals(BookingStatus.WAITING, dto.getStatus());
        assertEquals(1L, dto.getItem().getId());
        assertEquals(2L, dto.getBooker().getId());
    }

    @Test
    void updateBookingFromDto_shouldUpdateStatus() {
        User owner = new User(1L, "Owner", "owner@mail.com");
        User booker = new User(2L, "Booker", "booker@mail.com");
        Item item = new Item(1L, "Дрель", "Мощная дрель", true, owner, null);
        Booking booking = new Booking(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3),
                item,
                booker,
                BookingStatus.WAITING
        );
        UpdateBookingDto updateBookingDto = new UpdateBookingDto(BookingStatus.APPROVED);

        BookingMapper.updateBookingFromDto(updateBookingDto, booking);

        assertEquals(BookingStatus.APPROVED, booking.getStatus());
    }
}