package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithAdditionalInfoDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    private final ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @Test
    void shouldMapItemToItemDto() {
        User owner = User.builder()
                .id(1L)
                .name("user")
                .email("user@test.com")
                .build();

        Item item = Item.builder()
                .id(10L)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        ItemDto dto = itemMapper.toItemDto(item);

        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        assertEquals("item", dto.getName());
        assertEquals("description", dto.getDescription());
        assertTrue(dto.getAvailable());
        assertEquals(1L, dto.getOwnerId());
    }

    @Test
    void shouldMapCreateDtoToItem() {
        ItemCreateDto createDto = ItemCreateDto.builder()
                .name("item")
                .description("description")
                .available(true)
                .requestId(5L)
                .build();

        Item item = itemMapper.toItem(createDto);

        assertNotNull(item);
        assertEquals("item", item.getName());
        assertEquals("description", item.getDescription());
        assertTrue(item.isAvailable());
        assertNull(item.getOwner());
    }

    @Test
    void shouldMapItemToItemWithAdditionalInfoDto() {
        Item item = Item.builder()
                .id(3L)
                .name("item")
                .description("description")
                .available(false)
                .build();

        ItemWithAdditionalInfoDto dto = itemMapper.toItemWithAdditionalInfoDto(item);

        assertNotNull(dto);
        assertEquals(3L, dto.getId());
        assertEquals("item", dto.getName());
        assertEquals("description", dto.getDescription());
        assertFalse(dto.getAvailable());
        assertNull(dto.getLastBooking());
        assertNull(dto.getNextBooking());
        assertNull(dto.getComments());
    }

    @Test
    void shouldMapItemWithCommentsToItemWithAdditionalInfoDto() {
        User owner = User.builder()
                .id(1L)
                .name("user")
                .email("user@test.com")
                .build();

        Comment comment = Comment.builder()
                .id(5L)
                .text("Nice item")
                .created(LocalDateTime.now())
                .author(owner)
                .build();

        Item item = Item.builder()
                .id(10L)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner)
                .comments(List.of(comment))
                .build();

        ItemWithAdditionalInfoDto dto = itemMapper.toItemWithAdditionalInfoDto(item);

        assertNotNull(dto);
        assertNotNull(dto.getComments());
        assertEquals(1, dto.getComments().size());
        assertEquals(5L, dto.getComments().getFirst().getId());
        assertEquals("Nice item", dto.getComments().getFirst().getText());
        assertEquals(comment.getCreated(), dto.getComments().getFirst().getCreated());
    }
}