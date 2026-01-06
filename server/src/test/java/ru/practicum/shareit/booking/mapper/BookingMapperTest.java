package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    private final BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    @Test
    void shouldMapBookingToBookingDto() {
        User booker = User.builder()
                .id(1L)
                .name("John")
                .email("john@example.com")
                .build();

        Item item = Item.builder()
                .id(10L)
                .name("Drill")
                .description("Power drill")
                .available(true)
                .owner(booker)
                .build();

        Booking booking = Booking.builder()
                .id(100L)
                .item(item)
                .booker(booker)
                .start(LocalDateTime.of(2026, 1, 10, 10, 0))
                .end(LocalDateTime.of(2026, 1, 15, 10, 0))
                .build();

        BookingDto dto = bookingMapper.toBookingDto(booking);

        assertNotNull(dto);
        assertEquals(100L, dto.getId());
        assertNotNull(dto.getItem());
        assertEquals(10L, dto.getItem().getId());
        assertEquals("Drill", dto.getItem().getName());
        assertNotNull(dto.getBooker());
        assertEquals(1L, dto.getBooker().getId());
        assertEquals("John", dto.getBooker().getName());
        assertEquals(booking.getStart(), dto.getStart());
        assertEquals(booking.getEnd(), dto.getEnd());
    }

    @Test
    void shouldMapBookingCreateDtoToBooking() {
        BookingCreateDto createDto = BookingCreateDto.builder()
                .itemId(10L)
                .start(LocalDateTime.of(2026, 1, 10, 10, 0))
                .end(LocalDateTime.of(2026, 1, 15, 10, 0))
                .build();

        Booking booking = bookingMapper.toBooking(createDto);

        assertNotNull(booking);
        assertEquals(createDto.getStart(), booking.getStart());
        assertEquals(createDto.getEnd(), booking.getEnd());
        assertNull(booking.getBooker());
        assertNull(booking.getId());
    }
}