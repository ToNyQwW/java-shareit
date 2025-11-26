package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemCreateDto {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    private boolean available;

    private Long requestId;
}