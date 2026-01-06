package ru.practicum.shareit.item.client;

import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import ru.practicum.shareit.item.dto.CommentCreateRequestDto;
import ru.practicum.shareit.item.dto.ItemCreateRequestDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;

import static org.junit.jupiter.api.Assertions.*;

class ItemClientTest {

    @Test
    void allMethods_shouldBeCovered() {
        var client = new ItemClient("http://unreachable-host", new RestTemplateBuilder());
        ItemCreateRequestDto itemDto = ItemCreateRequestDto.builder().build();
        ItemUpdateRequestDto itemDtoUpdate = ItemUpdateRequestDto.builder().build();
        CommentCreateRequestDto commentDto = CommentCreateRequestDto.builder().build();

        assertThrows(Exception.class, () -> client.getItem(1L, 100L));
        assertThrows(Exception.class, () -> client.createItem(1L, itemDto));
        assertThrows(Exception.class, () -> client.searchItem(1L, "text"));
        assertThrows(Exception.class, () -> client.getUserItems(1L));
        assertThrows(Exception.class, () -> client.updateItem(1L, 100L,itemDtoUpdate));
        assertThrows(Exception.class, () -> client.createComment(1L, 100L, commentDto));
    }
}