package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static ru.practicum.shareit.util.ValidationConstants.MAX_SIZE_TEXT;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequestDto {

    @Size(max = MAX_SIZE_TEXT)
    @NotBlank
    private String text;
}