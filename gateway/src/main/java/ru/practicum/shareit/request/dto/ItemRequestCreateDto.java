package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import static ru.practicum.shareit.util.ValidationConstants.MAX_SIZE_TEXT;

@Data
public class ItemRequestCreateDto {

    @NotBlank
    @Size(max = MAX_SIZE_TEXT)
    private String description;
}