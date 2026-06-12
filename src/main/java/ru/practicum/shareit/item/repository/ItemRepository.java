package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemRepository {

    Item createItem(Item item);

    Item updateItem(Item item);

    Item getItemById(Long id);

    List<Item> findAllByOwnerId(Long ownerId);

    Collection<Item> search(String text);
}
