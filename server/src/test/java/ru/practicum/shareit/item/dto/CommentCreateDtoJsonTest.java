package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentCreateDtoJsonTest {

    private final JacksonTester<CommentCreateDto> json;

    @Test
    void testDeserialize() throws Exception {
        String jsonContent = """
        {
          "text": "This is a comment"
        }
        """;

        CommentCreateDto commentCreateDto = this.json.parseObject(jsonContent);

        assertThat(commentCreateDto.getText()).isEqualTo("This is a comment");
    }
}