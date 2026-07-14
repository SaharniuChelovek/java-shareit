package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.enums.BookingStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingStatusTest {

    @Test
    void bookingStatus_shouldHaveAllValues() {
        BookingStatus[] statuses = BookingStatus.values();

        assertEquals(4, statuses.length);
        assertEquals(BookingStatus.WAITING, BookingStatus.valueOf("WAITING"));
        assertEquals(BookingStatus.APPROVED, BookingStatus.valueOf("APPROVED"));
        assertEquals(BookingStatus.REJECTED, BookingStatus.valueOf("REJECTED"));
        assertEquals(BookingStatus.CANCELED, BookingStatus.valueOf("CANCELED"));
    }
}
