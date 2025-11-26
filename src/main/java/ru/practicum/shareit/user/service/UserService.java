package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

public interface UserService {

    UserDto createUser(UserCreateDto userCreateDto);

    UserDto getUser(long id);

    UserDto updateUser(long id, UserUpdateDto userUpdateDto);

    void deleteUser(long id);
}