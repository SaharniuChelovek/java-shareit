package ru.practicum.shareit;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemServiceImpl itemService;


    @Test
    void searchWhenTextMatches() {

        String searchText = "ДрЕлЬ";

        User owner = new User(1L, "Owner", "owner@mail.ru");
        Item foundItem = new Item(1L, "Ударная дрель", "Мощная дрель", true, owner, null);

        when(itemRepository.search(searchText)).thenReturn(List.of(foundItem));


        List<ItemDto> result = itemService.searchItems(searchText);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Ударная дрель", result.getFirst().getName());

        verify(itemRepository, times(1)).search(searchText);
    }

    @Test
    void searchWhenTextIsBlank() {

        String blankText = "   ";

        List<ItemDto> result = itemService.searchItems(blankText);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(itemRepository, never()).search(anyString());
    }

    @Test
    void searchWhenNothingMatches() {

        String searchText = "перфоратор";

        when(itemRepository.search(searchText)).thenReturn(Collections.emptyList());

        List<ItemDto> result = itemService.searchItems(searchText);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


    @Test
    void createItemWhenUserDoesNotExist() {

        Long fakeUserId = 999L;
        CreateItemDto createItemDto = new CreateItemDto("Молоток", "Хороший молоток", true, null);

        when(userRepository.getUserById(fakeUserId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createItem(fakeUserId, createItemDto));

        verify(itemRepository, never()).createItem(any(Item.class));
    }

    @Test
    void createItemWhenUserExists() {

        Long userId = 1L;
        User owner = new User(userId, "Owner", "owner@mail.ru");
        CreateItemDto createItemDto = new CreateItemDto("Молоток", "Хороший молоток", true, null);

        Item itemWithoutOwner = ItemMapper.toItem(createItemDto);

        Item savedItem = new Item(1L, "Молоток", "Хороший молоток", true, owner, null);

        when(userRepository.getUserById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.createItem(any(Item.class))).thenReturn(savedItem);


        ItemDto result = itemService.createItem(userId, createItemDto);


        assertNotNull(result);
        assertEquals(1L, result.getId());
    }
}
