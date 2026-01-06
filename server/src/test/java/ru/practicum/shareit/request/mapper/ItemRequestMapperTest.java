package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {

    private final ItemRequestMapper mapper = Mappers.getMapper(ItemRequestMapper.class);

    @Test
    void shouldMapItemRequestToItemRequestDto() {
        User requestor = User.builder()
                .id(1L)
                .name("user")
                .email("user@test.com")
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .id(10L)
                .description("Need item")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();

        ItemRequestDto dto = mapper.toItemRequestDto(itemRequest);

        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        assertEquals("Need item", dto.getDescription());
        assertEquals(1L, dto.getRequestorId());
        assertEquals(itemRequest.getCreated(), dto.getCreated());
    }

    @Test
    void shouldMapCreateDtoToItemRequest() {
        ItemRequestCreateDto createDto = ItemRequestCreateDto.builder()
                .description("description")
                .build();

        ItemRequest itemRequest = mapper.toItemRequest(createDto);

        assertNotNull(itemRequest);
        assertEquals("description", itemRequest.getDescription());
        assertNull(itemRequest.getId());
        assertNull(itemRequest.getRequestor());
        assertNull(itemRequest.getCreated());
    }

    @Test
    void shouldMapItemRequestToItemRequestWithItemsDto() {
        User requestor = User.builder()
                .id(2L)
                .name("user")
                .email("user@test.com")
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .id(20L)
                .description("Need item")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();

        ItemRequestWithItemsDto dto = mapper.toItemRequestWithItemsDto(itemRequest);

        assertNotNull(dto);
        assertEquals(20L, dto.getId());
        assertEquals("Need itemr", dto.getDescription());
        assertEquals(2L, dto.getRequestorId());
        assertEquals(itemRequest.getCreated(), dto.getCreated());
        assertNotNull(dto.getItems());
        assertTrue(dto.getItems().isEmpty());
    }

    @Test
    void shouldMapItemToItemDtoViaItemRequestWithItemsDto() {
        User requestor = User.builder()
                .id(1L)
                .name("user")
                .email("user@test.com")
                .build();

        Item item1 = Item.builder()
                .id(10L)
                .name("item1")
                .description("description1")
                .available(true)
                .build();

        Item item2 = Item.builder()
                .id(11L)
                .name("item2")
                .description("description2")
                .available(false)
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .id(100L)
                .description("need item")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .items(List.of(item1, item2))
                .build();

        ItemRequestWithItemsDto dto = mapper.toItemRequestWithItemsDto(itemRequest);

        assertNotNull(dto);
        assertNotNull(dto.getItems());
        assertEquals(2, dto.getItems().size());

        ItemDto dto1 = dto.getItems().getFirst();
        assertEquals(10L, dto1.getId());
        assertEquals("item1", dto1.getName());
        assertEquals("description1", dto1.getDescription());
        assertTrue(dto1.getAvailable());

        ItemDto dto2 = dto.getItems().get(1);
        assertEquals(11L, dto2.getId());
        assertEquals("item2", dto2.getName());
        assertEquals("description2", dto2.getDescription());
        assertFalse(dto2.getAvailable());
    }
}