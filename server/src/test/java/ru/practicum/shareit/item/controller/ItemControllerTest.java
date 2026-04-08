package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.util.RequestHeaderConstants.USER_ID_HEADER;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    @SneakyThrows
    void createItem_shouldReturnCreatedItem() {
        ItemCreateDto requestDto = ItemCreateDto.builder()
                .name("item")
                .description("description")
                .available(true)
                .build();

        ItemDto responseDto = ItemDto.builder()
                .id(1L)
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .available(requestDto.getAvailable())
                .ownerId(1L)
                .build();

        when(itemService.createItem(eq(1L), any(ItemCreateDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, 1L)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("item")))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.available", is(true)))
                .andExpect(jsonPath("$.ownerId", is(1)));
    }

    @Test
    @SneakyThrows
    void getItem_shouldReturnItemWithAdditionalInfo() {
        LocalDateTime created = LocalDateTime.now().withNano(0);

        CommentDto comment = CommentDto.builder()
                .id(1L)
                .text("text")
                .authorName("author")
                .created(created)
                .build();

        ItemWithAdditionalInfoDto responseDto = ItemWithAdditionalInfoDto.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .lastBooking(BookingDto.builder().id(1L).build())
                .nextBooking(BookingDto.builder().id(2L).build())
                .comments(List.of(comment))
                .build();

        when(itemService.getItem(1L, 1L))
                .thenReturn(responseDto);

        String expectedCreated = objectMapper.writeValueAsString(created).replace("\"", "");

        mockMvc.perform(get("/items/1")
                        .header(USER_ID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("item")))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.available", is(true)))
                .andExpect(jsonPath("$.lastBooking.id", is(1)))
                .andExpect(jsonPath("$.nextBooking.id", is(2)))
                .andExpect(jsonPath("$.comments[0].id", is(1)))
                .andExpect(jsonPath("$.comments[0].text", is("text")))
                .andExpect(jsonPath("$.comments[0].authorName", is("author")))
                .andExpect(jsonPath("$.comments[0].created", is(expectedCreated)));
    }

    @Test
    @SneakyThrows
    void searchItem_shouldReturnList() {
        ItemDto item1 = ItemDto.builder()
                .id(1L)
                .name("new item")
                .description("description")
                .available(true)
                .ownerId(1L)
                .build();

        ItemDto item2 = ItemDto.builder()
                .id(2L)
                .name("maybe item")
                .description("description")
                .available(true)
                .ownerId(2L)
                .build();

        when(itemService.searchItems("item"))
                .thenReturn(List.of(item1, item2));

        mockMvc.perform(get("/items/search")
                        .param("text", "item")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("new item")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("maybe item")));
    }

    @Test
    @SneakyThrows
    void getUserItems_shouldReturnList() {
        ItemWithAdditionalInfoDto item = ItemWithAdditionalInfoDto.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .comments(List.of())
                .build();

        when(itemService.getUserItems(1L))
                .thenReturn(List.of(item));

        mockMvc.perform(get("/items")
                        .header(USER_ID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("item")));
    }

    @Test
    @SneakyThrows
    void updateItem_shouldReturnUpdatedItem() {
        ItemUpdateDto updateDto = ItemUpdateDto.builder()
                .name("item updated")
                .description("description updated")
                .available(false)
                .build();

        ItemDto responseDto = ItemDto.builder()
                .id(1L)
                .name(updateDto.getName())
                .description(updateDto.getDescription())
                .available(updateDto.getAvailable())
                .ownerId(1L)
                .build();

        when(itemService.updateItem(eq(1L), eq(1L), any(ItemUpdateDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(patch("/items/1")
                        .header(USER_ID_HEADER, 1L)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("item updated")))
                .andExpect(jsonPath("$.description", is("description updated")))
                .andExpect(jsonPath("$.available", is(false)))
                .andExpect(jsonPath("$.ownerId", is(1)));
    }

    @Test
    @SneakyThrows
    void addComment_shouldReturnCreatedComment() {
        LocalDateTime created = LocalDateTime.now().withNano(0);

        CommentCreateDto requestDto = CommentCreateDto.builder()
                .text("item comment")
                .build();

        CommentDto responseDto = CommentDto.builder()
                .id(1L)
                .text(requestDto.getText())
                .authorName("author")
                .created(created)
                .build();

        when(itemService.createComment(eq(1L), eq(1L), any(CommentCreateDto.class)))
                .thenReturn(responseDto);

        String expectedCreated = objectMapper.writeValueAsString(created).replace("\"", "");

        mockMvc.perform(post("/items/1/comment")
                        .header(USER_ID_HEADER, 1L)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is("item comment")))
                .andExpect(jsonPath("$.authorName", is("author")))
                .andExpect(jsonPath("$.created", is(expectedCreated)));
    }
}