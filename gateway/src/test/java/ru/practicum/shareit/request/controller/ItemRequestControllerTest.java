package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestClient itemRequestClient;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Test
    void shouldCreateItemRequest() throws Exception {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Need a drill");

        Mockito.when(itemRequestClient.createItemRequest(anyLong(), any()))
                .thenReturn(ResponseEntity.ok("Created"));

        mockMvc.perform(post("/requests")
                        .header(USER_ID_HEADER, 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Created"));
    }

    @Test
    void shouldFailCreateItemRequestWithBlankDescription() throws Exception {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();

        mockMvc.perform(post("/requests")
                        .header(USER_ID_HEADER, 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetUserItemRequests() throws Exception {
        Mockito.when(itemRequestClient.getUserItemRequests(anyLong()))
                .thenReturn(ResponseEntity.ok("User requests"));

        mockMvc.perform(get("/requests")
                        .header(USER_ID_HEADER, 2L))
                .andExpect(status().isOk())
                .andExpect(content().string("User requests"));
    }

    @Test
    void shouldGetAllItemRequests() throws Exception {
        Mockito.when(itemRequestClient.getAllItemRequests(anyLong()))
                .thenReturn(ResponseEntity.ok("All requests"));

        mockMvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, 2L))
                .andExpect(status().isOk())
                .andExpect(content().string("All requests"));
    }

    @Test
    void shouldGetItemRequestById() throws Exception {
        Mockito.when(itemRequestClient.getItemRequest(anyLong()))
                .thenReturn(ResponseEntity.ok("Request info"));

        mockMvc.perform(get("/requests/10"))
                .andExpect(status().isOk())
                .andExpect(content().string("Request info"));
    }
}