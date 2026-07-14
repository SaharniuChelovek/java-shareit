package ru.practicum.shareit.repositorytest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserDbRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserDbRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(new User(null, "User", "user@mail.com"));
    }

    @Test
    void findByEmail_shouldReturnUser() {
        User found = userRepository.findByEmail("user@mail.com");

        assertNotNull(found);
        assertEquals("user@mail.com", found.getEmail());
    }

    @Test
    void findByEmail_whenNotExists_shouldReturnNull() {
        User found = userRepository.findByEmail("notexists@mail.com");

        assertNull(found);
    }
}