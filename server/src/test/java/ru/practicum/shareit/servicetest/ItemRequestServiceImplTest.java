package ru.practicum.shareit.servicetest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemDbRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserDbRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserDbRepository userRepository;

    @Mock
    private ItemDbRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User requestor;
    private User owner;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        requestor = new User(1L, "Requestor", "requestor@mail.com");
        owner = new User(2L, "Owner", "owner@mail.com");
        itemRequest = new ItemRequest(1L, "Нужна дрель", requestor, LocalDateTime.now());
    }

    @Test
    void createRequest_whenUserNotFound_shouldThrowNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.createRequest(99L, new CreateItemRequestDto("Нужна дрель")));
    }

    @Test
    void createRequest_shouldSaveAndReturnDto() {
        CreateItemRequestDto dto = new CreateItemRequestDto("Нужна дрель");

        when(userRepository.findById(requestor.getId())).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto result = itemRequestService.createRequest(requestor.getId(), dto);

        assertNotNull(result);
        assertEquals("Нужна дрель", result.getDescription());
        assertTrue(result.getItems().isEmpty());
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void getOwnRequests_whenUserNotFound_shouldThrowNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getOwnRequests(99L));
    }

    @Test
    void getOwnRequests_shouldReturnRequestsWithItems() {
        Item item = new Item(1L, "Дрель", "Мощная дрель", true, owner, itemRequest);

        when(userRepository.findById(requestor.getId())).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(requestor.getId()))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByRequestIdIn(List.of(itemRequest.getId())))
                .thenReturn(List.of(item));

        List<ItemRequestDto> result = itemRequestService.getOwnRequests(requestor.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Нужна дрель", result.getFirst().getDescription());
        assertEquals(1, result.getFirst().getItems().size());
        assertEquals("Дрель", result.getFirst().getItems().getFirst().getName());
    }

    @Test
    void getAllRequests_whenUserNotFound_shouldThrowNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getAllRequests(99L));
    }

    @Test
    void getAllRequests_shouldReturnOtherUsersRequests() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(owner.getId()))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByRequestIdIn(List.of(itemRequest.getId())))
                .thenReturn(new ArrayList<>());

        List<ItemRequestDto> result = itemRequestService.getAllRequests(owner.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Нужна дрель", result.getFirst().getDescription());
        assertTrue(result.getFirst().getItems().isEmpty());
    }

    @Test
    void getRequestById_whenUserNotFound_shouldThrowNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(99L, 1L));
    }

    @Test
    void getRequestById_whenRequestNotFound_shouldThrowNotFoundException() {
        when(userRepository.findById(requestor.getId())).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(requestor.getId(), 99L));
    }

    @Test
    void getRequestById_shouldReturnRequestWithItems() {
        Item item = new Item(1L, "Дрель", "Мощная дрель", true, owner, itemRequest);

        when(userRepository.findById(requestor.getId())).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestIdIn(List.of(itemRequest.getId())))
                .thenReturn(List.of(item));

        ItemRequestDto result = itemRequestService.getRequestById(requestor.getId(), itemRequest.getId());

        assertNotNull(result);
        assertEquals("Нужна дрель", result.getDescription());
        assertEquals(1, result.getItems().size());
        assertEquals("Дрель", result.getItems().getFirst().getName());
        assertEquals(owner.getId(), result.getItems().getFirst().getOwnerId());
    }
}
