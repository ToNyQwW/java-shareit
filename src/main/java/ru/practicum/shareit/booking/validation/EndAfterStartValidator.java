package ru.practicum.shareit.booking.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

public class EndAfterStartValidator implements ConstraintValidator<EndAfterStart, BookingCreateDto> {

    @Override
    public boolean isValid(BookingCreateDto dto, ConstraintValidatorContext context) {
        return dto.getEnd().isAfter(dto.getStart());
    }
}