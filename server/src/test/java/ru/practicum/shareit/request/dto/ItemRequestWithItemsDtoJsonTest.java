package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestWithItemsDtoJsonTest {

    @Autowired
    private final JacksonTester<ItemRequestWithItemsDto> json;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime created = LocalDateTime.of(2026, 1, 7, 15, 0, 0);

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .ownerId(2L)
                .build();

        ItemRequestWithItemsDto itemRequestWithItemsDto = ItemRequestWithItemsDto.builder()
                .id(3L)
                .description("Need item")
                .requestorId(4L)
                .created(created)
                .items(List.of(itemDto))
                .build();

        JsonContent<ItemRequestWithItemsDto> result = json.write(itemRequestWithItemsDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.requestorId");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).hasJsonPath("$.items");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(3);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Need item");
        assertThat(result).extractingJsonPathNumberValue("$.requestorId").isEqualTo(4);
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2026-01-07T15:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("item");
        assertThat(result).extractingJsonPathStringValue("$.items[0].description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.items[0].available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.items[0].ownerId").isEqualTo(2);
    }
}