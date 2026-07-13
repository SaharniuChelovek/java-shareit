package ru.practicum.shareit.repositorytest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemDbRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserDbRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserDbRepository userRepository;

    @Autowired
    private ItemDbRepository itemRepository;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(new User(null, "Owner", "owner@mail.com"));
        booker = userRepository.save(new User(null, "Booker", "booker@mail.com"));
        item = itemRepository.save(new Item(null, "Дрель", "Мощная дрель", true, owner, null));
    }

    @Test
    void findAllByBookerIdOrderByStartDateDesc_shouldReturnBookings() {
        bookingRepository.save(new Booking(
                null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3),
                item, booker, BookingStatus.WAITING));

        List<Booking> result = bookingRepository
                .findAllByBookerIdOrderByStartDateDesc(booker.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findAllByBookerIdAndEndDateBeforeOrderByStartDateDesc_shouldReturnPastBookings() {
        bookingRepository.save(new Booking(
                null,
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusDays(1),
                item, booker, BookingStatus.APPROVED));

        List<Booking> result = bookingRepository
                .findAllByBookerIdAndEndDateBeforeOrderByStartDateDesc(
                        booker.getId(), LocalDateTime.now());

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findAllByItemOwnerIdOrderByStartDateDesc_shouldReturnOwnerBookings() {
        bookingRepository.save(new Booking(
                null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3),
                item, booker, BookingStatus.WAITING));

        List<Booking> result = bookingRepository
                .findAllByItemOwnerIdOrderByStartDateDesc(owner.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void existsByItemIdAndBookerIdAndStatusAndEndDateBefore_shouldReturnTrue() {
        bookingRepository.save(new Booking(
                null,
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusDays(1),
                item, booker, BookingStatus.APPROVED));

        boolean exists = bookingRepository
                .existsByItemIdAndBookerIdAndStatusAndEndDateBefore(
                        item.getId(), booker.getId(),
                        BookingStatus.APPROVED, LocalDateTime.now());

        assertTrue(exists);
    }

    @Test
    void existsByItemIdAndBookerIdAndStatusAndEndDateBefore_shouldReturnFalse() {
        boolean exists = bookingRepository
                .existsByItemIdAndBookerIdAndStatusAndEndDateBefore(
                        item.getId(), booker.getId(),
                        BookingStatus.APPROVED, LocalDateTime.now());

        assertFalse(exists);
    }
}