package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    @SneakyThrows
    void createUser_shouldReturnCreatedUser() {
        UserCreateDto requestDto = UserCreateDto.builder()
                .name("Иван Иванов")
                .email("ivan@test.com")
                .build();

        UserDto responseDto = UserDto.builder()
                .id(1L)
                .name("Иван Иванов")
                .email("ivan@test.com")
                .build();

        when(userService.createUser(any(UserCreateDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Иван Иванов")))
                .andExpect(jsonPath("$.email", is("ivan@test.com")));
    }

    @Test
    @SneakyThrows
    void updateUser_shouldReturnUpdatedUser() {
        UserUpdateDto updateDto = UserUpdateDto.builder()
                .name("Обновленное имя")
                .build();

        UserDto responseDto = UserDto.builder()
                .id(1L)
                .name("Обновленное имя")
                .email("ivan@test.com")
                .build();

        when(userService.updateUser(eq(1L), any(UserUpdateDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(updateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Обновленное имя")))
                .andExpect(jsonPath("$.email", is("ivan@test.com")));
    }

    @Test
    @SneakyThrows
    void getUser_shouldReturnUser() {
        UserDto responseDto = UserDto.builder()
                .id(1L)
                .name("Иван Иванов")
                .email("ivan@test.com")
                .build();

        when(userService.getUser(eq(1L)))
                .thenReturn(responseDto);

        mockMvc.perform(get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Иван Иванов")));
    }

    @Test
    @SneakyThrows
    void deleteUser_shouldReturnNoContent() {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }
}