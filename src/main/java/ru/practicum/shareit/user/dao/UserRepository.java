package ru.practicum.shareit.user.dao;

import org.springframework.data.repository.CrudRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByEmail(String email);
}