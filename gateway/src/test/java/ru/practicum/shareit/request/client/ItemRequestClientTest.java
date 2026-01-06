package ru.practicum.shareit.request.client;

import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ItemRequestClientTest {

    @Test
    void allMethods_shouldBeCovered() {
        var client = new ItemRequestClient("http://unreachable-host", new RestTemplateBuilder());
        ItemRequestCreateDto dto = new ItemRequestCreateDto();

        assertThrows(Exception.class, () -> client.createItemRequest(1L, dto));
        assertThrows(Exception.class, () -> client.getUserItemRequests(1L));
        assertThrows(Exception.class, () -> client.getAllItemRequests(1L));
        assertThrows(Exception.class, () -> client.getItemRequest(1L));
    }
}