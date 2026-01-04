package ru.practicum.shareit.booking.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;

public class EndAfterStartValidator implements ConstraintValidator<EndAfterStart, BookingCreateRequestDto> {

    @Override
    public boolean isValid(BookingCreateRequestDto dto, ConstraintValidatorContext context) {
        return dto.getEnd().isAfter(dto.getStart());
    }
}