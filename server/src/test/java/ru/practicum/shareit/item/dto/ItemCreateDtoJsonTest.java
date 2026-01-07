package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemCreateDtoJsonTest {

    private final JacksonTester<ItemCreateDto> json;

    @Test
    void testDeserialize() throws Exception {
        String jsonContent = """
                {
                  "name": "Item Name",
                  "description": "Item Description",
                  "available": true,
                  "requestId": 10
                }
                """;

        ItemCreateDto itemCreateDto = this.json.parseObject(jsonContent);

        assertThat(itemCreateDto.getName()).isEqualTo("Item Name");
        assertThat(itemCreateDto.getDescription()).isEqualTo("Item Description");
        assertThat(itemCreateDto.getAvailable()).isTrue();
        assertThat(itemCreateDto.getRequestId()).isEqualTo(10L);
    }
}