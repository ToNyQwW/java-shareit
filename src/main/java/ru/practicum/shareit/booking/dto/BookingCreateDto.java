package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingCreateDto {

    @NotNull
    @FutureOrPresent
    private LocalDateTime start;

    @Future
    @NotNull
    private LocalDateTime end;

    private long itemId;
}