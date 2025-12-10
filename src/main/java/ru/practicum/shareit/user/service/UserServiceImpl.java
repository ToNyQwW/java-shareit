package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserCreateDto userCreateDto) {
        throwIfEmailExists(userCreateDto.getEmail());

        User user = userMapper.toUser(userCreateDto);
        User createdUser = userRepository.save(user);
        log.info("User created: {}", user);

        return userMapper.toUserDto(createdUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUser(long id) {
        User user = getUserOrElseThrow(id);
        log.info("User get: {}", user);

        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(long id, UserUpdateDto userUpdateDto) {
        User user = getUserOrElseThrow(id);

        updateUserFields(user, userUpdateDto);
        userRepository.save(user);
        log.info("User updated: {}", user);

        return userMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
        log.info("User deleted: {}", id);
    }

    private void updateUserFields(User user, UserUpdateDto userUpdateDto) {
        String name = userUpdateDto.getName();
        if (name != null && !name.isBlank()) {
            user.setName(name);
        }

        String email = userUpdateDto.getEmail();
        if (email != null && !email.equals(user.getEmail())) {
            throwIfEmailExists(email);
            user.setEmail(email);
        }
    }

    private User getUserOrElseThrow(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
    }

    private void throwIfEmailExists(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new DuplicateEmailException("User with email " + email + " already exists");
        }
    }
}