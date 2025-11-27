package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ItemCreateDto {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    private boolean available;
}