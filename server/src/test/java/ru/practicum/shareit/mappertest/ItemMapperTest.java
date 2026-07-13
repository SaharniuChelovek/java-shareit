package ru.practicum.shareit.mappertest;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemMapperTest {

    @Test
    void toItem_shouldMapCorrectly() {
        CreateItemDto dto = new CreateItemDto("Дрель", "Мощная дрель", true, null);

        Item item = ItemMapper.toItem(dto);

        assertNull(item.getId());
        assertEquals("Дрель", item.getName());
        assertEquals("Мощная дрель", item.getDescription());
        assertTrue(item.isAvailable());
    }

    @Test
    void toItemDto_shouldMapCorrectly() {
        User owner = new User(1L, "Owner", "owner@mail.com");
        Item item = new Item(1L, "Дрель", "Мощная дрель", true, owner, null);

        ItemDto dto = ItemMapper.toItemDto(item);

        assertEquals(1L, dto.getId());
        assertEquals("Дрель", dto.getName());
        assertEquals("Мощная дрель", dto.getDescription());
        assertTrue(dto.getAvailable());
        assertEquals(1L, dto.getOwnerId());
        assertNull(dto.getRequestId());
        assertTrue(dto.getComments().isEmpty());
    }

    @Test
    void toItemDto_shouldMapRequestIdCorrectly() {
        User owner = new User(1L, "Owner", "owner@mail.com");
        ItemRequest request = new ItemRequest(1L, "Нужна дрель", owner, LocalDateTime.now());
        Item item = new Item(1L, "Дрель", "Мощная дрель", true, owner, request);

        ItemDto dto = ItemMapper.toItemDto(item);

        assertEquals(1L, dto.getRequestId());
    }

    @Test
    void updateItemFromDto_shouldUpdateOnlyNonNullFields() {
        User owner = new User(1L, "Owner", "owner@mail.com");
        Item item = new Item(1L, "Дрель", "Старое описание", true, owner, null);
        UpdateItemDto updateItemDto = new UpdateItemDto("Новое название", null, null);

        ItemMapper.updateItemFromDto(updateItemDto, item);

        assertEquals("Новое название", item.getName());
        assertEquals("Старое описание", item.getDescription());
        assertTrue(item.isAvailable());
    }
}
