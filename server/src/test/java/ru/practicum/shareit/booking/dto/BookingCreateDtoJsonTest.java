package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingCreateDtoJsonTest {

    private final JacksonTester<BookingCreateDto> json;

    @Test
    void testDeserialize() throws Exception {
        String jsonContent =
                "{ \"start\": \"2026-01-07T10:00:00\", "
                + "\"end\": \"2026-01-08T10:00:00\", "
                + "\"itemId\": 1 }";

        BookingCreateDto bookingCreateDto = json.parseObject(jsonContent);

        assertThat(bookingCreateDto.getStart()).isEqualTo(LocalDateTime.of(2026, 1, 7, 10, 0));
        assertThat(bookingCreateDto.getEnd()).isEqualTo(LocalDateTime.of(2026, 1, 8, 10, 0));
        assertThat(bookingCreateDto.getItemId()).isEqualTo(1L);
    }
}