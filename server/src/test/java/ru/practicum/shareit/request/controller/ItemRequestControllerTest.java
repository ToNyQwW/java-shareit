package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.util.RequestHeaderConstants.USER_ID_HEADER;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    @SneakyThrows
    void createItemRequest_shouldReturnCreatedRequest() {
        LocalDateTime created = LocalDateTime.now();

        ItemRequestCreateDto requestDto = ItemRequestCreateDto.builder()
                .description("need item")
                .build();

        ItemRequestDto responseDto = ItemRequestDto.builder()
                .id(1L)
                .description(requestDto.getDescription())
                .requestorId(1L)
                .created(created)
                .build();

        when(itemRequestService.createItemRequest(eq(1L), any(ItemRequestCreateDto.class)))
                .thenReturn(responseDto);

        String expectedCreated = objectMapper.writeValueAsString(created).replace("\"", "");

        mockMvc.perform(post("/requests")
                        .header(USER_ID_HEADER, 1L)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("need item")))
                .andExpect(jsonPath("$.requestorId", is(1)))
                .andExpect(jsonPath("$.created", is(expectedCreated)));
    }

    @Test
    @SneakyThrows
    void getUserItemRequests_shouldReturnList() {
        LocalDateTime created = LocalDateTime.now();

        ItemRequestWithItemsDto request = ItemRequestWithItemsDto.builder()
                .id(1L)
                .description("need item")
                .requestorId(1L)
                .created(created)
                .items(List.of())
                .build();

        when(itemRequestService.getUserItemRequests(1L))
                .thenReturn(List.of(request));

        String expectedCreated = objectMapper.writeValueAsString(created).replace("\"", "");

        mockMvc.perform(get("/requests")
                        .header(USER_ID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("need item")))
                .andExpect(jsonPath("$[0].requestorId", is(1)))
                .andExpect(jsonPath("$[0].created", is(expectedCreated)))
                .andExpect(jsonPath("$[0].items", hasSize(0)));
    }

    @Test
    @SneakyThrows
    void getAllItemRequests_shouldReturnList() {
        ItemRequestDto request = ItemRequestDto.builder()
                .id(1L)
                .description("need item")
                .requestorId(2L)
                .build();

        when(itemRequestService.getAllItemRequests(1L))
                .thenReturn(List.of(request));

        mockMvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("need item")))
                .andExpect(jsonPath("$[0].requestorId", is(2)));
    }

    @Test
    @SneakyThrows
    void getItemRequest_shouldReturnRequestWithItems() {
        LocalDateTime created = LocalDateTime.now().withNano(0);

        ItemDto item = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .ownerId(2L)
                .build();

        ItemRequestWithItemsDto responseDto = ItemRequestWithItemsDto.builder()
                .id(1L)
                .description("need item")
                .requestorId(1L)
                .created(created)
                .items(List.of(item))
                .build();

        when(itemRequestService.getItemRequest(1L))
                .thenReturn(responseDto);

        String expectedCreated = objectMapper.writeValueAsString(created).replace("\"", "");

        mockMvc.perform(get("/requests/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("need item")))
                .andExpect(jsonPath("$.requestorId", is(1)))
                .andExpect(jsonPath("$.created", is(expectedCreated)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].id", is(1)))
                .andExpect(jsonPath("$.items[0].name", is("item")))
                .andExpect(jsonPath("$.items[0].available", is(true)));
    }
}