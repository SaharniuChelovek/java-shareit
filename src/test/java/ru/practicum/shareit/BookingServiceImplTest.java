package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemDbRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserDbRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;


@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemDbRepository itemRepository;

    @Mock
    private UserDbRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User booker;
    private User owner;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "Owner", "owner@mail.ru");
        booker = new User(2L, "Booker", "booker@mail.ru");
        item = new Item(1L, "Дрель", "Мощная дрель", true, owner, null);
        booking = new Booking(1L, LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1), item, booker, BookingStatus.WAITING);
    }


    @Test
    void createBookingWhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(99L, new CreateBookingDto()));
    }

    @Test
    void createBookingWhenItemNotFound() {
        CreateBookingDto dto = new CreateBookingDto(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                99L);

        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(booker.getId(), dto));
    }

    @Test
    void createBookingWhenItemNotAvailable() {
        item.setAvailable(false);
        CreateBookingDto dto = new CreateBookingDto(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item.getId());

        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(booker.getId(), dto));
    }

    @Test
    void createBookingSuccess() {
        CreateBookingDto dto = new CreateBookingDto(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item.getId());

        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.createBooking(booker.getId(), dto);

        assertNotNull(result);
        assertEquals(BookingStatus.WAITING, booking.getStatus());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void approveBookingWhenBookingNotFound() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(owner.getId(), 99L, true));
    }

    @Test
    void approveBookingWhenUserIsNotOwner() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(booker.getId(), booking.getId(), true));
    }

    @Test
    void approveBookingWhenApprovedTrue() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.approveBooking(owner.getId(), booking.getId(), true);

        assertNotNull(result);
        assertEquals(BookingStatus.APPROVED, booking.getStatus());
    }

    @Test
    void approveBookingWhenApprovedFalse() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.approveBooking(owner.getId(), booking.getId(), false);

        assertNotNull(result);
        assertEquals(BookingStatus.REJECTED, booking.getStatus());
    }

    // --- getBookingById ---

    @Test
    void getBookingByIdWhenUserIsNotBookerOrOwner() {
        Long strangerUserId = 99L;

        when(bookingRepository.findByIdAndBookerIdOrIdAndItemOwnerId(
                booking.getId(), strangerUserId, booking.getId(), strangerUserId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(strangerUserId, booking.getId()));
    }

    @Test
    void getBookingByIdWhenUserIsBooker() {
        when(bookingRepository.findByIdAndBookerIdOrIdAndItemOwnerId(
                booking.getId(), booker.getId(), booking.getId(), booker.getId()))
                .thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBookingById(booker.getId(), booking.getId());

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
    }

    // --- getBookings ---

    @Test
    void getBookingsWhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.getBookings(99L, BookingState.ALL));
    }

    @Test
    void getBookingsAll() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdOrderByStartDateDesc(booker.getId()))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookings(booker.getId(), BookingState.ALL);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository, times(1)).findAllByBookerIdOrderByStartDateDesc(booker.getId());
    }

    @Test
    void getBookingsCurrent() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
                eq(booker.getId()), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookings(booker.getId(), BookingState.CURRENT);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getBookingsWaiting() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDateDesc(
                booker.getId(), BookingStatus.WAITING))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookings(booker.getId(), BookingState.WAITING);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStatusOrderByStartDateDesc(booker.getId(), BookingStatus.WAITING);
    }

    // --- getOwnerBookings ---

    @Test
    void getOwnerBookingsWhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.getOwnerBookings(99L, BookingState.ALL));
    }

    @Test
    void getOwnerBookingsAll() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDateDesc(owner.getId()))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getOwnerBookings(owner.getId(), BookingState.ALL);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
