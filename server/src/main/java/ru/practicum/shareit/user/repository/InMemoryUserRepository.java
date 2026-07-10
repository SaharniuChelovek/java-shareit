package ru.practicum.shareit.user.repository;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@Qualifier("InMemoryUserRepository")
public class InMemoryUserRepository implements UserRepository {

    Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        log.info("Создается пользователь {}", user);

        user.setId(getNextId());

        users.put(user.getId(), user);
        log.info("Пользователь {} успешно создан", user);
        return user;
    }

    @Override
    public User update(User user) {
        log.info("Обновление пользователя {}", user);

        users.put(user.getId(), user);
        log.info("Пользователь {} обновлен", user);
        return user;
    }

    @Override
    public Optional<User> getUserById(Long id) {

        log.info("Получение пользователя по id {}", id);

        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void delete(Long id) {
        log.info("Удаление пользователя с id {}", id);

        users.remove(id);
        log.info("Пользователь с id {} удален", id);
    }

    @Override
    public User findByEmail(String email) {
        log.info("Поиск пользователя по email {}", email);
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElse(null); // Вернет null, если не нашли
    }

    private Long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
