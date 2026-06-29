package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByIdAndBookerIdOrIdAndItemOwnerId(Long id, Long bookerId, Long id2, Long ownerId);

    List<Booking> findAllByBookerIdOrderByStartDateDesc(Long bookerId);

    List<Booking> findAllByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
            Long bookerId, LocalDateTime now1, LocalDateTime now2);

    List<Booking> findAllByBookerIdAndEndDateBeforeOrderByStartDateDesc(
            Long bookerId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStartDateAfterOrderByStartDateDesc(
            Long bookerId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDateDesc(
            Long bookerId, BookingStatus status);

    List<Booking> findAllByItemOwnerIdOrderByStartDateDesc(Long ownerId);

    List<Booking> findAllByItemOwnerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
            Long ownerId, LocalDateTime now1, LocalDateTime now2);

    List<Booking> findAllByItemOwnerIdAndEndDateBeforeOrderByStartDateDesc(
            Long ownerId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStartDateAfterOrderByStartDateDesc(
            Long ownerId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDateDesc(
            Long ownerId, BookingStatus status);

    Optional<Booking> findFirstByItemIdAndStatusAndEndDateBeforeOrderByEndDateDesc(
            Long itemId, BookingStatus status, LocalDateTime now);

    Optional<Booking> findFirstByItemIdAndStatusAndStartDateAfterOrderByStartDateAsc(
            Long itemId, BookingStatus status, LocalDateTime now);

    boolean existsByItemIdAndStatusAndStartDateBeforeAndEndDateAfter(
            Long itemId, BookingStatus status, LocalDateTime end, LocalDateTime start);

    List<Booking> findAllByItemIdInAndStatus(List<Long> itemIds, BookingStatus status);

    //for comments
    List<Booking> findAllByItemIdInAndStatusAndEndDateBeforeOrderByEndDateDesc(
            List<Long> itemIds, BookingStatus status, LocalDateTime now);

    List<Booking> findAllByItemIdInAndStatusAndStartDateAfterOrderByStartDateAsc(
            List<Long> itemIds, BookingStatus status, LocalDateTime now);

    boolean existsByItemIdAndBookerIdAndStatusAndEndDateBefore(
            Long itemId, Long bookerId, BookingStatus status, LocalDateTime now);
}
