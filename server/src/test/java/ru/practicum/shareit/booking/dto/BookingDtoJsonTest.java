package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingDtoJsonTest {

    private final JacksonTester<BookingDto> json;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime startTime = LocalDateTime.of(2026, 1, 7, 10, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2026, 1, 8, 10, 0, 0);

        BookingItemDto item = BookingItemDto.builder()
                .id(1L)
                .name("item")
                .build();

        BookingUserDto booker = BookingUserDto.builder()
                .id(2L)
                .name("booker")
                .build();

        BookingDto bookingDto = BookingDto.builder()
                .id(3L)
                .start(startTime)
                .end(endTime)
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.item.id");
        assertThat(result).hasJsonPath("$.item.name");
        assertThat(result).hasJsonPath("$.booker.id");
        assertThat(result).hasJsonPath("$.booker.name");
        assertThat(result).hasJsonPath("$.status");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(3);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2026-01-07T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2026-01-08T10:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("item");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("booker");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }
}