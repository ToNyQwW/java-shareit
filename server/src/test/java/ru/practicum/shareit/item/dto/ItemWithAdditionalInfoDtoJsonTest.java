package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemWithAdditionalInfoDtoJsonTest {

    private final JacksonTester<ItemWithAdditionalInfoDto> json;

    @Test
    void testSerialize() throws Exception {
        BookingDto lastBooking = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2026, 1, 7, 10, 0))
                .end(LocalDateTime.of(2026, 1, 8, 10, 0))
                .build();

        BookingDto nextBooking = BookingDto.builder()
                .id(2L)
                .start(LocalDateTime.of(2026, 1, 9, 10, 0))
                .end(LocalDateTime.of(2026, 1, 10, 10, 0))
                .build();

        CommentDto comment = CommentDto.builder()
                .id(1L)
                .text("comment")
                .authorName("author name")
                .created(LocalDateTime.of(2026, 1, 6, 12, 0))
                .build();

        ItemWithAdditionalInfoDto itemDto = ItemWithAdditionalInfoDto.builder()
                .id(3L)
                .name("item")
                .description("description")
                .available(true)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(List.of(comment))
                .build();

        JsonContent<ItemWithAdditionalInfoDto> result = json.write(itemDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.lastBooking.id");
        assertThat(result).hasJsonPath("$.nextBooking.id");
        assertThat(result).hasJsonPath("$.comments[0].id");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(3);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("comment");
    }
}