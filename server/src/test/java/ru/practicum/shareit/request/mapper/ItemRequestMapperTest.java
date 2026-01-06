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
                .name("John")
                .email("john@example.com")
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .id(10L)
                .description("Need a drill")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();

        ItemRequestDto dto = mapper.toItemRequestDto(itemRequest);

        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        assertEquals("Need a drill", dto.getDescription());
        assertEquals(1L, dto.getRequestorId());
        assertEquals(itemRequest.getCreated(), dto.getCreated());
    }

    @Test
    void shouldMapCreateDtoToItemRequest() {
        ItemRequestCreateDto createDto = ItemRequestCreateDto.builder()
                .description("Looking for a bike")
                .build();

        ItemRequest itemRequest = mapper.toItemRequest(createDto);

        assertNotNull(itemRequest);
        assertEquals("Looking for a bike", itemRequest.getDescription());
        assertNull(itemRequest.getId());
        assertNull(itemRequest.getRequestor());
        assertNull(itemRequest.getCreated());
    }

    @Test
    void shouldMapItemRequestToItemRequestWithItemsDto() {
        User requestor = User.builder()
                .id(2L)
                .name("Alice")
                .email("alice@example.com")
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .id(20L)
                .description("Need a ladder")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();

        ItemRequestWithItemsDto dto = mapper.toItemRequestWithItemsDto(itemRequest);

        assertNotNull(dto);
        assertEquals(20L, dto.getId());
        assertEquals("Need a ladder", dto.getDescription());
        assertEquals(2L, dto.getRequestorId());
        assertEquals(itemRequest.getCreated(), dto.getCreated());
        assertNotNull(dto.getItems());
        assertTrue(dto.getItems().isEmpty());
    }

    @Test
    void shouldMapItemToItemDtoViaItemRequestWithItemsDto() {
        User requestor = User.builder()
                .id(1L)
                .name("Alice")
                .email("alice@example.com")
                .build();

        Item item1 = Item.builder()
                .id(10L)
                .name("Drill")
                .description("Power drill")
                .available(true)
                .build();

        Item item2 = Item.builder()
                .id(11L)
                .name("Ladder")
                .description("10m ladder")
                .available(false)
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .id(100L)
                .description("Need tools")
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
        assertEquals("Drill", dto1.getName());
        assertEquals("Power drill", dto1.getDescription());
        assertTrue(dto1.getAvailable());

        ItemDto dto2 = dto.getItems().get(1);
        assertEquals(11L, dto2.getId());
        assertEquals("Ladder", dto2.getName());
        assertEquals("10m ladder", dto2.getDescription());
        assertFalse(dto2.getAvailable());
    }
}