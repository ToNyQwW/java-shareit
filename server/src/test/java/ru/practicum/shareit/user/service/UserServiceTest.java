package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private UserCreateDto userCreateDto;
    private UserUpdateDto userUpdateDto;


    @BeforeEach
    void setUp() {

        user = User.builder()
                .name("user")
                .email("user@test.com")
                .build();
        userRepository.save(user);

        userCreateDto = UserCreateDto.builder()
                .name("userDto")
                .email("userDto@test.com")
                .build();

        userUpdateDto = UserUpdateDto.builder()
                .name("userUpdateDto")
                .email("userUpdateDto@test.com")
                .build();
    }

    @Test
    void createUser_whenEmailExists_thenDuplicateEmailExceptionThrown() {
        String email = user.getEmail();
        userCreateDto.setEmail(email);

        DuplicateEmailException duplicateEmailException = assertThrows(DuplicateEmailException.class,
                () -> userService.createUser(userCreateDto));
        assertEquals("User with email " + email + " already exists", duplicateEmailException.getMessage());
    }

    @Test
    void createUser_shouldCorrectlyCreateUser() {
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
        UserDto foundedUser = userService.getUser(user.getId());

        assertEquals(user.getId(), foundedUser.getId());
        assertEquals(user.getName(), foundedUser.getName());
        assertEquals(user.getEmail(), foundedUser.getEmail());
    }

    @Test
    void updateUser_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 10000L;

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> userService.updateUser(userId, userUpdateDto));
        assertEquals("User with id " + userId + " not found", notFoundException.getMessage());
    }


    @Test
    void updateUser_whenEmailExists_thenDuplicateEmailExceptionThrown() {
        UserCreateDto newUser = UserCreateDto.builder()
                .name("user")
                .email("updateUser@test.com")
                .build();
        UserDto savedUser = userService.createUser(newUser);
        String email = savedUser.getEmail();
        userUpdateDto.setEmail(email);

        DuplicateEmailException duplicateEmailException = assertThrows(DuplicateEmailException.class,
                () -> userService.updateUser(user.getId(), userUpdateDto));
        assertEquals("User with email " + email + " already exists", duplicateEmailException.getMessage());
    }

    @Test
    void updateUser_shouldCorrectlyUpdateUser() {
        UserDto userDto = userService.updateUser(user.getId(), userUpdateDto);

        assertEquals(userUpdateDto.getName(), userDto.getName());
        assertEquals(userUpdateDto.getEmail(), userDto.getEmail());
    }
}