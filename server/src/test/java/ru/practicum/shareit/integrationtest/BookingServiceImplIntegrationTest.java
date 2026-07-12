package ru.practicum.shareit.integrationtest;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemDbRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserDbRepository;

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
    private UserDbRepository userRepository;

    @Autowired
    private ItemDbRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(new User(null, "Owner", "owner@mail.ru"));
        booker = userRepository.save(new User(null, "Booker", "booker@mail.ru"));
        item = itemRepository.save(new Item(null, "Дрель", "Мощная дрель", true, owner, null));
    }

    @Test
    void getBookings_shouldReturnAllBookings() {
        bookingRepository.save(new Booking(
                null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3),
                item,
                booker,
                BookingStatus.WAITING
        ));

        List<BookingDto> result = bookingService.getBookings(booker.getId(), BookingState.ALL);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getBookings_shouldReturnPastBookings() {
        bookingRepository.save(new Booking(
                null,
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusDays(1),
                item,
                booker,
                BookingStatus.APPROVED
        ));

        List<BookingDto> result = bookingService.getBookings(booker.getId(), BookingState.PAST);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getOwnerBookings_shouldReturnAllOwnerBookings() {
        bookingRepository.save(new Booking(
                null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3),
                item,
                booker,
                BookingStatus.WAITING
        ));

        List<BookingDto> result = bookingService.getOwnerBookings(owner.getId(), BookingState.ALL);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getOwnerBookings_shouldReturnFutureBookings() {
        bookingRepository.save(new Booking(
                null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3),
                item,
                booker,
                BookingStatus.APPROVED
        ));

        List<BookingDto> result = bookingService.getOwnerBookings(owner.getId(), BookingState.FUTURE);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(BookingStatus.APPROVED, result.getFirst().getStatus());
    }
}
