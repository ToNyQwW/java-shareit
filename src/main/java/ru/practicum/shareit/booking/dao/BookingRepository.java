package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @EntityGraph(attributePaths = {"item", "booker"})
    @Query("""
            SELECT b FROM Booking b
            WHERE b.booker.id = :bookerId
            AND b.start <= :now AND b.end >= :now
            """)
    List<Booking> findCurrentByBookerId(@Param("bookerId") long id, @Param("now") LocalDateTime now, Sort sort);

    @EntityGraph(attributePaths = {"item", "booker"})
    @Query("""
            SELECT b FROM Booking b
            WHERE b.item.owner.id = :ownerId
            AND b.start <= :now AND b.end >= :now
            """)
    List<Booking> findCurrentByOwnerId(@Param("ownerId") long id, @Param("now") LocalDateTime now, Sort sort);

    @EntityGraph(attributePaths = "item")
    List<Booking> findAllByItemId(long id);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findAllByItemOwnerId(long id);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findAllByBookerId(long id, Sort sort);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findAllByBookerIdAndStatus(long id, BookingStatus status, Sort sort);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findAllByBookerIdAndEndBefore(long id, LocalDateTime now, Sort sort);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findAllByBookerIdAndStartAfter(long id, LocalDateTime now, Sort sort);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findAllByItemOwnerId(long id, Sort sort);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findAllByItemOwnerIdAndStatus(long id, BookingStatus status, Sort sort);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findAllByItemOwnerIdAndEndBefore(long id, LocalDateTime now, Sort sort);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findAllByItemOwnerIdAndStartAfter(long id, LocalDateTime now, Sort sort);
}