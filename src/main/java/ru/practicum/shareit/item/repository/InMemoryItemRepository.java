package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Component
@Qualifier("InMemoryItemRepository")
public class InMemoryItemRepository implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item createItem(Item item) {

        log.info("Создание предмета {}", item.getId());

        item.setId(getNextId());

        items.put(item.getId(), item);
        log.info("{}", item.getId());

        return item;
    }

    @Override
    public Item updateItem(Item item) {
        log.info("Обновление предмета {}", item.getId());

        items.put(item.getId(), item);
        log.info("{}", item.getId());

        return item;
    }

    @Override
    public Optional<Item> getItemById(Long id) {
        log.info("Получение вещи по id {}", id);

        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> findAllByOwnerId(Long ownerId) {
        return items.values().stream()

                .filter(item -> ownerId.equals(item.getOwner().getId()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> search(String text) {

        String lowerText = text.toLowerCase();
        return items.values().stream()
                .filter(item -> item.isAvailable() &&
                        (item.getName().toLowerCase().contains(lowerText) ||
                                item.getDescription().toLowerCase().contains(lowerText)))
                .collect(Collectors.toList());
    }

    private Long getNextId() {
        long currentMaxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
