package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import static ru.practicum.shareit.util.ValidationConstants.MAX_SIZE_TEXT;

@Data
@Builder
public class ItemUpdateRequestDto {

    @Size(max = MAX_SIZE_TEXT)
    private String name;

    @Size(max = MAX_SIZE_TEXT)
    private String description;

    private Boolean available;
}