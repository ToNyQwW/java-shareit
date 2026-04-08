package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.validation.EndAfterStart;

import java.time.LocalDateTime;

@Data
@Builder
@EndAfterStart
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreateRequestDto {

    @NotNull
    @FutureOrPresent
    private LocalDateTime start;

    @Future
    @NotNull
    private LocalDateTime end;

    private long itemId;
}