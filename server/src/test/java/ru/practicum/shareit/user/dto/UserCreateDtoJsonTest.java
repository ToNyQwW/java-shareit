package ru.practicum.shareit.user.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserCreateDtoJsonTest {

    private final JacksonTester<UserCreateDto> json;

    @Test
    void testDeserialize() throws Exception {
        String jsonContent = "{ \"name\": \"User name\", \"email\": \"user@test.com\" }";

        UserCreateDto result = json.parseObject(jsonContent);

        assertThat(result.getName()).isEqualTo("User name");
        assertThat(result.getEmail()).isEqualTo("user@test.com");
    }
}