package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // GET /bookings/{bookingId} — поиск по id бронирования и id букера или владельца вещи
    Optional<Booking> findByIdAndBookerIdOrIdAndItemOwnerId(Long id, Long bookerId, Long id2, Long ownerId);

    // GET /bookings?state=ALL
    List<Booking> findAllByBookerIdOrderByStartDateDesc(Long bookerId);

    // GET /bookings?state=CURRENT
    List<Booking> findAllByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
            Long bookerId, LocalDateTime now1, LocalDateTime now2);

    // GET /bookings?state=PAST
    List<Booking> findAllByBookerIdAndEndDateBeforeOrderByStartDateDesc(
            Long bookerId, LocalDateTime now);

    // GET /bookings?state=FUTURE
    List<Booking> findAllByBookerIdAndStartDateAfterOrderByStartDateDesc(
            Long bookerId, LocalDateTime now);

    // GET /bookings?state=WAITING или state=REJECTED
    List<Booking> findAllByBookerIdAndStatusOrderByStartDateDesc(
            Long bookerId, BookingStatus status);

    // GET /bookings/owner?state=ALL
    List<Booking> findAllByItemOwnerIdOrderByStartDateDesc(Long ownerId);

    // GET /bookings/owner?state=CURRENT
    List<Booking> findAllByItemOwnerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
            Long ownerId, LocalDateTime now1, LocalDateTime now2);

    // GET /bookings/owner?state=PAST
    List<Booking> findAllByItemOwnerIdAndEndDateBeforeOrderByStartDateDesc(
            Long ownerId, LocalDateTime now);

    // GET /bookings/owner?state=FUTURE
    List<Booking> findAllByItemOwnerIdAndStartDateAfterOrderByStartDateDesc(
            Long ownerId, LocalDateTime now);

    // GET /bookings/owner?state=WAITING или state=REJECTED
    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDateDesc(
            Long ownerId, BookingStatus status);
}
