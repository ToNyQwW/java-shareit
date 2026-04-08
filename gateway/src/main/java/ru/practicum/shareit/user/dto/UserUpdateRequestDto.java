package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import static ru.practicum.shareit.util.ValidationConstants.MAX_SIZE_TEXT;

@Data
@Builder
public class UserUpdateRequestDto {

    @Size(max = MAX_SIZE_TEXT)
    private String name;

    @Email
    @Size(max = MAX_SIZE_TEXT)
    private String email;
}