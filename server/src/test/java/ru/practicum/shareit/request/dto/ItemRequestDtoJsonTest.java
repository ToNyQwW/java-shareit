package ru.practicum.shareit.request.dto;

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
class ItemRequestDtoJsonTest {

    private final JacksonTester<ItemRequestDto> json;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime createdDate = LocalDateTime.of(2026, 1, 7, 15, 0, 0);

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .requestorId(2L)
                .created(createdDate)
                .build();

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).hasJsonPathStringValue("$.description");
        assertThat(result).hasJsonPathNumberValue("$.requestorId");
        assertThat(result).hasJsonPathStringValue("$.created");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathNumberValue("$.requestorId").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2026-01-07T15:00:00");
    }
}