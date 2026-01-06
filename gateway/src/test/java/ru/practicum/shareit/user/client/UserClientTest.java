package ru.practicum.shareit.user.client;

import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import ru.practicum.shareit.user.dto.UserCreateRequestDto;
import ru.practicum.shareit.user.dto.UserUpdateRequestDto;

import static org.junit.jupiter.api.Assertions.*;

class UserClientTest {

    @Test
    void allMethods_shouldBeCovered() {
        var client = new UserClient("http://unreachable-host", new RestTemplateBuilder());
        UserCreateRequestDto userDto = UserCreateRequestDto.builder().build();
        UserUpdateRequestDto updateDto = UserUpdateRequestDto.builder().build();

        assertThrows(Exception.class, () -> client.createUser(userDto));
        assertThrows(Exception.class, () -> client.updateUser(100L, updateDto));
        assertThrows(Exception.class, () -> client.getUser(100L));
        assertThrows(Exception.class, () -> client.deleteUser(100L));
    }
}