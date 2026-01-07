package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import static ru.practicum.shareit.util.ValidationConstants.MAX_SIZE_TEXT;

@Data
@Builder
public class ItemCreateRequestDto {

    @NotBlank
    @Size(max = MAX_SIZE_TEXT)
    private String name;

    @NotBlank
    @Size(max = MAX_SIZE_TEXT)
    private String description;

    @NotNull
    private Boolean available;

    private Long requestId;
}