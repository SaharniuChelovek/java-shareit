package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {

    Collection<User> findAll();

    User create(User user);

    User update(User newUser);

    Optional<User> getUserById(Long id);

    void delete(Long id);

    User findByEmail(String email);
}
