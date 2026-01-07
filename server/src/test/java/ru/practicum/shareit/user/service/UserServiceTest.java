package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    void setUp() {
        userRepository.save(
                User.builder()
                        .name("user")
                        .email("user@test.com")
                        .build()
        );
    }

    @Test
    void createUser_whenEmailExists_thenDuplicateEmailExceptionThrown() {
        User user = userRepository.findById(1L).orElseThrow();
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .name("userDto")
                .email(user.getEmail())
                .build();

        DuplicateEmailException duplicateEmailException = assertThrows(DuplicateEmailException.class,
                () -> userService.createUser(userCreateDto));
        assertEquals("User with email " + user.getEmail() +
                " already exists", duplicateEmailException.getMessage());
    }

    @Test
    void createUser_shouldCorrectlyCreateUser() {
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .name("userDto")
                .email("userDto@test.com")
                .build();
        UserDto createUser = userService.createUser(userCreateDto);

        assertEquals(userCreateDto.getName(), createUser.getName());
        assertEquals(userCreateDto.getEmail(), createUser.getEmail());
    }

    @Test
    void getUser_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 10000L;

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> userService.getUser(userId));
        assertEquals("User with id " + userId + " not found", notFoundException.getMessage());
    }

    @Test
    void getUser_shouldCorrectlyGetUser() {
        User user = userRepository.findById(1L).orElseThrow();
        UserDto foundedUser = userService.getUser(1L);

        assertEquals(user.getId(), foundedUser.getId());
        assertEquals(user.getName(), foundedUser.getName());
        assertEquals(user.getEmail(), foundedUser.getEmail());
    }

    @Test
    void updateUser_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 10000L;
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .name("userUpdate")
                .email("userUpdate@test.com")
                .build();

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> userService.updateUser(userId, userUpdateDto));
        assertEquals("User with id " + userId + " not found", notFoundException.getMessage());
    }

    @Test
    void updateUser_whenEmailExists_thenDuplicateEmailExceptionThrown() {
        UserCreateDto newUserCreateDto = UserCreateDto.builder()
                .name("newUser")
                .email("updateUser@test.com")
                .build();
        UserDto savedUser = userService.createUser(newUserCreateDto);

        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .name("userUpdateDto")
                .email(savedUser.getEmail())
                .build();

        User existingUser = userRepository.findById(1L).orElseThrow();

        DuplicateEmailException duplicateEmailException = assertThrows(DuplicateEmailException.class,
                () -> userService.updateUser(existingUser.getId(), userUpdateDto));
        assertEquals("User with email " + savedUser.getEmail() + " already exists", duplicateEmailException.getMessage());
    }

    @Test
    void updateUser_shouldCorrectlyUpdateUser() {
        User existingUser = userRepository.findById(1L).orElseThrow();

        UserUpdateDto updateDto = UserUpdateDto.builder()
                .name("userUpdateDto")
                .email("userUpdateDto@test.com")
                .build();

        UserDto updatedUser = userService.updateUser(existingUser.getId(), updateDto);

        assertEquals(updateDto.getName(), updatedUser.getName());
        assertEquals(updateDto.getEmail(), updatedUser.getEmail());
    }
}