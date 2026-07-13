package ru.practicum.shareit.integrationtest;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemDbRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserDbRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserDbRepository userRepository;

    @Autowired
    private ItemDbRepository itemRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User requestor;
    private User owner;

    @BeforeEach
    void setUp() {
        requestor = userRepository.save(new User(null, "Requestor", "requestor@mail.com"));
        owner = userRepository.save(new User(null, "Owner", "owner@mail.com"));
    }

    @Test
    void createRequest_shouldSaveAndReturnRequest() {
        CreateItemRequestDto dto = new CreateItemRequestDto("Нужна дрель");

        ItemRequestDto result = itemRequestService.createRequest(requestor.getId(), dto);

        assertNotNull(result);
        assertEquals("Нужна дрель", result.getDescription());
        assertNotNull(result.getCreated());
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void getOwnRequests_shouldReturnRequestsWithItems() {
        ItemRequest request = itemRequestRepository.save(new ItemRequest(
                null, "Нужна дрель", requestor, LocalDateTime.now()
        ));

        itemRepository.save(new Item(null, "Дрель", "Мощная дрель", true, owner, request));

        List<ItemRequestDto> result = itemRequestService.getOwnRequests(requestor.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Нужна дрель", result.getFirst().getDescription());
        assertEquals(1, result.getFirst().getItems().size());
        assertEquals("Дрель", result.getFirst().getItems().getFirst().getName());
    }

    @Test
    void getAllRequests_shouldReturnOtherUsersRequests() {
        itemRequestRepository.save(new ItemRequest(
                null, "Нужна дрель", requestor, LocalDateTime.now()
        ));

        List<ItemRequestDto> result = itemRequestService.getAllRequests(owner.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Нужна дрель", result.getFirst().getDescription());
    }

    @Test
    void getAllRequests_shouldNotReturnOwnRequests() {
        itemRequestRepository.save(new ItemRequest(
                null, "Нужна дрель", requestor, LocalDateTime.now()
        ));

        List<ItemRequestDto> result = itemRequestService.getAllRequests(requestor.getId());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getRequestById_shouldReturnRequestWithItems() {
        ItemRequest request = itemRequestRepository.save(new ItemRequest(
                null, "Нужна дрель", requestor, LocalDateTime.now()
        ));

        itemRepository.save(new Item(null, "Дрель", "Мощная дрель", true, owner, request));

        ItemRequestDto result = itemRequestService.getRequestById(owner.getId(), request.getId());

        assertNotNull(result);
        assertEquals("Нужна дрель", result.getDescription());
        assertEquals(1, result.getItems().size());
        assertEquals("Дрель", result.getItems().getFirst().getName());
    }
}
