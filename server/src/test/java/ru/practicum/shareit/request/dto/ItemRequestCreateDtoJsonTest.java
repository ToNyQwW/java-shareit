package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestCreateDtoJsonTest {

    private final JacksonTester<ItemRequestCreateDto> json;

    @Test
    void testDeserialize() throws Exception {
        String jsonContent = """
                {
                  "description": "Request description"
                }
                """;

        ItemRequestCreateDto result = json.parseObject(jsonContent);

        assertThat(result.getDescription()).isEqualTo("Request description");
    }
}