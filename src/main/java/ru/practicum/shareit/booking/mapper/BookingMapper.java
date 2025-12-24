package ru.practicum.shareit.booking.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(source = "item.id", target = "item.id")
    @Mapping(source = "item.name", target = "item.name")
    @Mapping(source = "booker.id", target = "booker.id")
    @Mapping(source = "booker.name", target = "booker.name")
    BookingDto toBookingDto(Booking booking);

    Booking toBooking(BookingCreateDto bookingCreateDto);
}