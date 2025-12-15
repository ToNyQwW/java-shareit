package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
                SELECT b
                FROM Booking b
                JOIN FETCH b.item i
                JOIN FETCH b.booker u
                WHERE i.owner.id = :userId
            """)
    List<Booking> findAllByItemOwnerId(@Param("userId") long userId);

    List<Booking> findAllByItemId(long id);
}