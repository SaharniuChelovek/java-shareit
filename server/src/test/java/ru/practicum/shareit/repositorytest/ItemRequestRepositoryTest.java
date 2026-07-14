package ru.practicum.shareit.repositorytest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserDbRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserDbRepository userRepository;

    private User requestor;
    private User otherUser;

    @BeforeEach
    void setUp() {
        requestor = userRepository.save(new User(null, "Requestor", "requestor@mail.com"));
        otherUser = userRepository.save(new User(null, "Other", "other@mail.com"));
        itemRequestRepository.save(
                new ItemRequest(null, "Нужна дрель", requestor, LocalDateTime.now()));
    }

    @Test
    void findAllByRequestorIdOrderByCreatedDesc_shouldReturnOwnRequests() {
        List<ItemRequest> result = itemRequestRepository
                .findAllByRequestorIdOrderByCreatedDesc(requestor.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Нужна дрель", result.getFirst().getDescription());
    }

    @Test
    void findAllByRequestorIdNotOrderByCreatedDesc_shouldReturnOtherRequests() {
        List<ItemRequest> result = itemRequestRepository
                .findAllByRequestorIdNotOrderByCreatedDesc(otherUser.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Нужна дрель", result.getFirst().getDescription());
    }

    @Test
    void findAllByRequestorIdNotOrderByCreatedDesc_shouldNotReturnOwnRequests() {
        List<ItemRequest> result = itemRequestRepository
                .findAllByRequestorIdNotOrderByCreatedDesc(requestor.getId());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
