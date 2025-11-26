package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@Builder
@EqualsAndHashCode(of = "id")
public class ItemRequestDto {

    private Long id;

    @NotBlank
    private String description;

    @NotNull
    private Long requestor;

    @NotNull
    private LocalDateTime created;
}