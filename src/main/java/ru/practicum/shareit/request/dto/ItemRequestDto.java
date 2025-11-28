package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequestDto {

    private Long id;

    @NotBlank
    private String description;

    @NotNull
    private Long requestor;

    @NotNull
    private LocalDateTime created;
}