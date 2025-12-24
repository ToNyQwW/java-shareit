package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static ru.practicum.shareit.util.RequestHeaderConstants.USER_ID_HEADER;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(@RequestHeader(USER_ID_HEADER) long userId,
                                    @RequestBody @Valid BookingCreateDto bookingCreateDto) {
        return bookingService.createBooking(userId, bookingCreateDto);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader(USER_ID_HEADER) long userId,
                                 @PathVariable long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestHeader(USER_ID_HEADER) long userId,
                                            @RequestParam(name = "state", defaultValue = "ALL") BookingState state) {
        return bookingService.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestHeader(USER_ID_HEADER) long userId,
                                             @RequestParam(name = "state", defaultValue = "ALL") BookingState state) {
        return bookingService.getOwnerBookings(userId, state);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader(USER_ID_HEADER) long userId,
                                     @PathVariable long bookingId,
                                     @RequestParam(name = "approved") boolean approved) {
        return bookingService.approveBooking(userId, bookingId, approved);
    }
}