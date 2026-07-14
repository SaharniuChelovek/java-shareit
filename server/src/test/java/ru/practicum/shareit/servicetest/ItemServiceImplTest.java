package ru.practicum.shareit.servicetest;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ValidationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemDbRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserDbRepository;

import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemDbRepository itemRepository;

    @Mock
    private UserDbRepository userRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

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

        when(userRepository.findById(fakeUserId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createItem(fakeUserId, createItemDto));

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void createItemWhenUserExists() {

        Long userId = 1L;
        User owner = new User(userId, "Owner", "owner@mail.ru");
        CreateItemDto createItemDto = new CreateItemDto("Молоток", "Хороший молоток", true, null);

        Item itemWithoutOwner = ItemMapper.toItem(createItemDto);

        Item savedItem = new Item(1L, "Молоток", "Хороший молоток", true, owner, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(savedItem);

        ItemDto result = itemService.createItem(userId, createItemDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    // createItem с requestId
    @Test
    void createItemWhenRequestExists() {
        Long userId = 1L;
        User owner = new User(userId, "Owner", "owner@mail.ru");
        CreateItemDto createItemDto = new CreateItemDto("Молоток", "Хороший молоток", true, 1L);
        ItemRequest request = new ItemRequest(1L, "Нужен молоток", owner, LocalDateTime.now());
        Item savedItem = new Item(1L, "Молоток", "Хороший молоток", true, owner, request);

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(itemRepository.save(any(Item.class))).thenReturn(savedItem);

        ItemDto result = itemService.createItem(userId, createItemDto);

        assertNotNull(result);
        assertEquals(1L, result.getRequestId());
    }

    @Test
    void createItemWhenRequestNotFound() {
        Long userId = 1L;
        User owner = new User(userId, "Owner", "owner@mail.ru");
        CreateItemDto createItemDto = new CreateItemDto("Молоток", "Хороший молоток", true, 99L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createItem(userId, createItemDto));
    }

    // updateItem
    @Test
    void updateItemWhenItemNotFound() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.updateItem(1L, 99L, new UpdateItemDto("Новое", null, null)));
    }

    @Test
    void updateItemWhenUserIsNotOwner() {
        User owner = new User(1L, "Owner", "owner@mail.ru");
        Item item = new Item(1L, "Молоток", "Хороший молоток", true, owner, null);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class,
                () -> itemService.updateItem(99L, 1L, new UpdateItemDto("Новое", null, null)));
    }

    @Test
    void updateItemSuccess() {
        User owner = new User(1L, "Owner", "owner@mail.ru");
        Item item = new Item(1L, "Молоток", "Хороший молоток", true, owner, null);
        Item updatedItem = new Item(1L, "Новый молоток", "Хороший молоток", true, owner, null);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);

        ItemDto result = itemService.updateItem(1L, 1L, new UpdateItemDto("Новый молоток", null, null));

        assertNotNull(result);
        assertEquals("Новый молоток", result.getName());
    }

    // getItemById
    @Test
    void getItemByIdWhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemById(99L, 1L));
    }

    @Test
    void getItemByIdWhenItemNotFound() {
        User owner = new User(1L, "Owner", "owner@mail.ru");
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemById(1L, 99L));
    }

    @Test
    void getItemByIdSuccess() {
        User owner = new User(1L, "Owner", "owner@mail.ru");
        Item item = new Item(1L, "Молоток", "Хороший молоток", true, owner, null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(1L)).thenReturn(new ArrayList<>());
        when(bookingRepository.findFirstByItemIdAndStatusAndEndDateBeforeOrderByEndDateDesc(
                anyLong(), any(), any())).thenReturn(Optional.empty());
        when(bookingRepository.findFirstByItemIdAndStatusAndStartDateAfterOrderByStartDateAsc(
                anyLong(), any(), any())).thenReturn(Optional.empty());

        ItemDto result = itemService.getItemById(1L, 1L);

        assertNotNull(result);
        assertEquals("Молоток", result.getName());
    }

    // getItemsByOwner
    @Test
    void getItemsByOwnerWhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemsByOwner(99L));
    }

    @Test
    void getItemsByOwnerSuccess() {
        User owner = new User(1L, "Owner", "owner@mail.ru");
        Item item = new Item(1L, "Молоток", "Хороший молоток", true, owner, null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of(item));
        when(bookingRepository.findAllByItemIdInAndStatus(anyList(), any())).thenReturn(new ArrayList<>());
        when(commentRepository.findAllByItemIdIn(anyList())).thenReturn(new ArrayList<>());

        List<ItemDto> result = itemService.getItemsByOwner(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // addComment
    @Test
    void addCommentWhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.addComment(99L, 1L, new CreateCommentDto("Отличная вещь!")));
    }

    @Test
    void addCommentWhenItemNotFound() {
        User booker = new User(1L, "Booker", "booker@mail.ru");
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.addComment(1L, 99L, new CreateCommentDto("Отличная вещь!")));
    }

    @Test
    void addCommentWhenNoCompletedBooking() {
        User booker = new User(1L, "Booker", "booker@mail.ru");
        User owner = new User(2L, "Owner", "owner@mail.ru");
        Item item = new Item(1L, "Молоток", "Хороший молоток", true, owner, null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndDateBefore(
                anyLong(), anyLong(), any(), any())).thenReturn(false);

        assertThrows(ValidationException.class,
                () -> itemService.addComment(1L, 1L, new CreateCommentDto("Отличная вещь!")));
    }

    @Test
    void addCommentSuccess() {
        User booker = new User(1L, "Booker", "booker@mail.ru");
        User owner = new User(2L, "Owner", "owner@mail.ru");
        Item item = new Item(1L, "Молоток", "Хороший молоток", true, owner, null);
        Comment comment = new Comment(1L, "Отличная вещь!", item, booker, LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndDateBefore(
                anyLong(), anyLong(), any(), any())).thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = itemService.addComment(1L, 1L, new CreateCommentDto("Отличная вещь!"));

        assertNotNull(result);
        assertEquals("Отличная вещь!", result.getText());
    }
}
