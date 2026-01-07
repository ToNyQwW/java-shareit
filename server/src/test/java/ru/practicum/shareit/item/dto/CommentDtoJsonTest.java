package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentDtoJsonTest {

    private final JacksonTester<CommentDto> json;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime createdTime = LocalDateTime.of(2026, 1, 7, 16, 30, 0);

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("comment")
                .authorName("Author Name")
                .created(createdTime)
                .build();

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.text");
        assertThat(result).hasJsonPath("$.authorName");
        assertThat(result).hasJsonPath("$.created");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("comment");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Author Name");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2026-01-07T16:30:00");
    }
}