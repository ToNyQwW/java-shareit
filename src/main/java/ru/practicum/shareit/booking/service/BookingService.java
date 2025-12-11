package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(long userId, BookingCreateDto bookingCreateDto);

    BookingDto getBooking(long userId, long bookingId);

    List<BookingDto> getUserBookings(long userId, BookingState state);

    List<BookingDto> getOwnerBookings(long userId, BookingState state);

    BookingDto approveBooking(Long userId, long bookingId, boolean approved);
}