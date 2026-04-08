package ru.practicum.shareit.user.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserUpdateDtoJsonTest {

    private final JacksonTester<UserUpdateDto> json;

    @Test
    void testDeserialize() throws Exception {
        String jsonContent = "{ \"name\": \"Updated name\", \"email\": \"updated@test.com\" }";

        UserUpdateDto result = json.parseObject(jsonContent);

        assertThat(result.getName()).isEqualTo("Updated name");
        assertThat(result.getEmail()).isEqualTo("updated@test.com");
    }
}