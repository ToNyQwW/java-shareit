package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {

    private long id;

    @NotBlank
    private String text;

    @NotBlank
    private String authorName;

    @NotNull
    private LocalDateTime created;
}