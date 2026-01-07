package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemUpdateDtoJsonTest {

    private final JacksonTester<ItemUpdateDto> json;

    @Test
    void testDeserialize() throws Exception {
        String jsonContent = """
        {
          "name": "Updated Name",
          "description": "Updated Description",
          "available": true
        }
        """;

        ItemUpdateDto itemUpdateDto = this.json.parseObject(jsonContent);

        assertThat(itemUpdateDto.getName()).isEqualTo("Updated Name");
        assertThat(itemUpdateDto.getDescription()).isEqualTo("Updated Description");
        assertThat(itemUpdateDto.getAvailable()).isTrue();
    }
}