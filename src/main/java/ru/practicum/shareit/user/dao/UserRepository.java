package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserRepository {

    User createUser(User user);

    Optional<User> getUser(long id);

    void updateUser(User user);

    void deleteUser(long id);
}