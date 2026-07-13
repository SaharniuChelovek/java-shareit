package ru.practicum.shareit.repositorytest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemDbRepository;
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
class ItemRepositoryTest {

    @Autowired
    private ItemDbRepository itemRepository;

    @Autowired
    private UserDbRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(new User(null, "Owner", "owner@mail.com"));
        item = itemRepository.save(new Item(null, "Дрель", "Мощная дрель", true, owner, null));
    }

    @Test
    void findAllByOwnerId_shouldReturnItems() {
        List<Item> items = itemRepository.findAllByOwnerId(owner.getId());

        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals("Дрель", items.getFirst().getName());
    }

    @Test
    void search_shouldReturnMatchingItems() {
        List<Item> items = itemRepository.search("дрель");

        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals("Дрель", items.getFirst().getName());
    }

    @Test
    void search_shouldReturnEmptyWhenNotMatches() {
        List<Item> items = itemRepository.search("перфоратор");

        assertNotNull(items);
        assertTrue(items.isEmpty());
    }

    @Test
    void search_shouldNotReturnUnavailableItems() {
        itemRepository.save(new Item(null, "Перфоратор", "Мощный перфоратор", false, owner, null));

        List<Item> items = itemRepository.search("перфоратор");

        assertTrue(items.isEmpty());
    }

    @Test
    void findAllByRequestIdIn_shouldReturnItems() {
        User requestor = userRepository.save(new User(null, "Requestor", "requestor@mail.com"));
        ItemRequest request = itemRequestRepository.save(
                new ItemRequest(null, "Нужна дрель", requestor, LocalDateTime.now()));
        itemRepository.save(new Item(null, "Дрель2", "Мощная дрель", true, owner, request));

        List<Item> items = itemRepository.findAllByRequestIdIn(List.of(request.getId()));

        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals("Дрель2", items.getFirst().getName());
    }
}
