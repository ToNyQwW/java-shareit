package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentCreateRequestDto;
import ru.practicum.shareit.item.dto.ItemCreateRequestDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemClient itemClient;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Test
    void shouldCreateItem() throws Exception {
        ItemCreateRequestDto dto = ItemCreateRequestDto.builder()
                .name("item")
                .description("description")
                .available(true)
                .requestId(5L)
                .build();

        Mockito.when(itemClient.createItem(anyLong(), any()))
                .thenReturn(ResponseEntity.ok("Created"));

        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Created"));
    }

    @Test
    void shouldGetItemById() throws Exception {
        Mockito.when(itemClient.getItem(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok("Item info"));

        mockMvc.perform(get("/items/10")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Item info"));
    }

    @Test
    void shouldSearchItemByText() throws Exception {
        Mockito.when(itemClient.searchItem(anyLong(), anyString()))
                .thenReturn(ResponseEntity.ok("Search results"));

        mockMvc.perform(get("/items/search")
                        .header(USER_ID_HEADER, 2L)
                        .param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(content().string("Search results"));
    }

    @Test
    void shouldGetUserItems() throws Exception {
        Mockito.when(itemClient.getUserItems(anyLong()))
                .thenReturn(ResponseEntity.ok("User items"));

        mockMvc.perform(get("/items")
                        .header(USER_ID_HEADER, 2L))
                .andExpect(status().isOk())
                .andExpect(content().string("User items"));
    }

    @Test
    void shouldUpdateItem() throws Exception {
        ItemUpdateRequestDto dto = ItemUpdateRequestDto.builder().build();
        dto.setName("Updated item");
        dto.setDescription("Updated description");
        dto.setAvailable(false);

        Mockito.when(itemClient.updateItem(anyLong(), anyLong(), any()))
                .thenReturn(ResponseEntity.ok("Updated"));

        mockMvc.perform(patch("/items/10")
                        .header(USER_ID_HEADER, 3L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Updated"));
    }

    @Test
    void shouldCreateComment() throws Exception {
        CommentCreateRequestDto dto = CommentCreateRequestDto.builder()
                .text("Nice item!")
                .build();

        Mockito.when(itemClient.createComment(anyLong(), anyLong(), any(CommentCreateRequestDto.class)))
                .thenReturn(ResponseEntity.ok("Comment created"));

        mockMvc.perform(post("/items/10/comment")
                        .header(USER_ID_HEADER, 3L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Comment created"));
    }

    @Test
    void shouldFailCreateItemWithInvalidDto() throws Exception {
        ItemCreateRequestDto dto = ItemCreateRequestDto.builder().build();

        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailCreateCommentWithBlankText() throws Exception {
        CommentCreateRequestDto dto = CommentCreateRequestDto.builder().build();

        mockMvc.perform(post("/items/10/comment")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}