package ru.practicum.shareit.user.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ =  @Autowired)
class UserDtoJsonTest {

    private final JacksonTester<UserDto> json;

    @Test
    void testSerialize() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("User name")
                .email("user@test.com")
                .build();

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.email");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("User name");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("user@test.com");
    }
}