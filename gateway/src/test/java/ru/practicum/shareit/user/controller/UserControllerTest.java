package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserCreateRequestDto;
import ru.practicum.shareit.user.dto.UserUpdateRequestDto;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateUser() throws Exception {
        UserCreateRequestDto dto = UserCreateRequestDto
                .builder()
                .name("Alice")
                .email("alice@example.com")
                .build();

        Mockito.when(userClient.createUser(any()))
                .thenReturn(ResponseEntity.ok("User created"));

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("User created"));
    }

    @Test
    void shouldFailCreateUserWithInvalidDto() throws Exception {
        UserCreateRequestDto dto = UserCreateRequestDto.builder().build();

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetUserById() throws Exception {
        Mockito.when(userClient.getUser(anyLong()))
                .thenReturn(ResponseEntity.ok("User info"));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("User info"));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        UserUpdateRequestDto dto = UserUpdateRequestDto.builder().build();
        dto.setName("Updated Alice");
        dto.setEmail("updated@example.com");

        Mockito.when(userClient.updateUser(anyLong(), any()))
                .thenReturn(ResponseEntity.ok("User updated"));

        mockMvc.perform(patch("/users/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("User updated"));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        Mockito.when(userClient.deleteUser(anyLong()))
                .thenReturn(ResponseEntity.ok("User deleted"));

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted"));
    }
}