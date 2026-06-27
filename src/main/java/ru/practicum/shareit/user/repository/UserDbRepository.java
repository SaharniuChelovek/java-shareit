package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.User;


public interface UserDbRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
}
