package ru.practicum.shareit.booking.client;

import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.model.BookingState;

import static org.junit.jupiter.api.Assertions.*;

class BookingClientTest {

    @Test
    void allMethods_shouldBeCovered() {
        var client = new BookingClient("http://unreachable-host", new RestTemplateBuilder());
        BookingCreateRequestDto dto = BookingCreateRequestDto.builder().build();

        assertThrows(Exception.class, () -> client.getUserBookings(1L, BookingState.ALL));
        assertThrows(Exception.class, () -> client.getOwnerBookings(1L, BookingState.CURRENT));
        assertThrows(Exception.class, () -> client.createBooking(1L, dto));
        assertThrows(Exception.class, () -> client.approveBooking(1L, 100L, true));
        assertThrows(Exception.class, () -> client.approveBooking(1L, 100L, false));
        assertThrows(Exception.class, () -> client.getBooking(1L, 100L));
    }
}