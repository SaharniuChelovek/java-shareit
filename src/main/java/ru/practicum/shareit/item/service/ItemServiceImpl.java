package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemServiceImpl(final ItemRepository itemRepository, final UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }


    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {

        User owner = userRepository.getUserById(userId);
        if (owner == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        Item item = ItemMapper.toItem(itemDto);

        item.setOwner(owner);

        Item savedItem = itemRepository.createItem(item);

        return ItemMapper.toItemDto(savedItem);
    }


    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {

        Item oldItem = itemRepository.getItemById(itemId);
        if (oldItem == null) {
            throw new NotFoundException("Вещь не найдена");
        }

        if (!oldItem.getOwner().getId().equals(userId)) {

            throw new NotFoundException("Редактировать вещь может только владелец");
        }

        if (itemDto.getName() != null) {
            oldItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            oldItem.setDescription(itemDto.getDescription());
        }
        // Для boolean нужно проверять не null (примитив), поэтому в DTO лучше сделать Boolean available
        if (itemDto.getAvailable() != null) {
            oldItem.setAvailable(itemDto.getAvailable());
        }

        // 4. Сохраняем обновленную вещь
        Item updatedItem = itemRepository.updateItem(oldItem);
        return ItemMapper.toItemDto(updatedItem);
    }


    @Override
    public ItemDto getItemById(Long userId, Long itemId) {

        // 1. Проверяем, что пользователь, делающий запрос, существует
        // (Если кто-то шлет левый ID в заголовке, мы не дадим ему смотреть вещи)
        if (userRepository.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        // 2. Ищем саму вещь по itemId
        Item item = itemRepository.getItemById(itemId);
        if (item == null) {
            throw new NotFoundException("Вещь не найдена");
        }

        // 3. Проверку на владельца НЕ делаем! Любой может смотреть.

        // 4. Возвращаем DTO
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long userId) {

        if (userRepository.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        return itemRepository.findAllByOwnerId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {

        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }


        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
