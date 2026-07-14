package ru.practicum.shareit.integrationtest;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private EntityManager entityManager;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = new User(null, "Owner", "owner@mail.ru");
        entityManager.persist(owner);

        booker = new User(null, "Booker", "booker@mail.ru");
        entityManager.persist(booker);

        item = new Item(null, "Дрель", "Мощная дрель", true, owner, null);
        entityManager.persist(item);

        entityManager.flush();
    }

    @Test
    void getBookings_shouldReturnAllBookings() {
        Booking booking = new Booking(
                null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3),
                item, booker, BookingStatus.WAITING
        );
        entityManager.persist(booking);
        entityManager.flush();

        List<BookingDto> result = bookingService.getBookings(booker.getId(), BookingState.ALL);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getBookings_shouldReturnPastBookings() {
        Booking booking = new Booking(
                null,
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusDays(1),
                item, booker, BookingStatus.APPROVED
        );
        entityManager.persist(booking);
        entityManager.flush();

        List<BookingDto> result = bookingService.getBookings(booker.getId(), BookingState.PAST);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getOwnerBookings_shouldReturnAllOwnerBookings() {
        Booking booking = new Booking(
                null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3),
                item, booker, BookingStatus.WAITING
        );
        entityManager.persist(booking);
        entityManager.flush();

        List<BookingDto> result = bookingService.getOwnerBookings(owner.getId(), BookingState.ALL);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getOwnerBookings_shouldReturnFutureBookings() {
        Booking booking = new Booking(
                null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3),
                item, booker, BookingStatus.APPROVED
        );
        entityManager.persist(booking);
        entityManager.flush();

        List<BookingDto> result = bookingService.getOwnerBookings(owner.getId(), BookingState.FUTURE);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(BookingStatus.APPROVED, result.getFirst().getStatus());
    }
}
