package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import static ru.practicum.shareit.util.ValidationConstants.MAX_SIZE_TEXT;

@Data
@Builder
public class UserCreateRequestDto {

    @NotNull
    @Size(max = MAX_SIZE_TEXT)
    private String name;

    @Email
    @NotBlank
    @Size(max = MAX_SIZE_TEXT)
    private String email;
}