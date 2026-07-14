package ru.practicum.shareit.integrationtest;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private EntityManager entityManager;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = new User(null, "Owner", "owner@mail.com");
        entityManager.persist(owner);

        booker = new User(null, "Booker", "booker@mail.com");
        entityManager.persist(booker);

        item = new Item(null, "Дрель", "Мощная дрель", true, owner, null);
        entityManager.persist(item);

        entityManager.flush();
    }

    @Test
    void getItemsByOwner_shouldReturnItemsWithBookingsAndComments() {
        Booking pastBooking = new Booking(
                null,
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusDays(1),
                item, booker, BookingStatus.APPROVED
        );
        entityManager.persist(pastBooking);

        Booking futureBooking = new Booking(
                null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3),
                item, booker, BookingStatus.APPROVED
        );
        entityManager.persist(futureBooking);

        Comment comment = new Comment(null, "Отличная дрель!", item, booker, LocalDateTime.now());
        entityManager.persist(comment);

        entityManager.flush();

        List<ItemDto> result = itemService.getItemsByOwner(owner.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Дрель", result.getFirst().getName());
        assertEquals(pastBooking.getEndDate(), result.getFirst().getLastBooking());
        assertEquals(futureBooking.getStartDate(), result.getFirst().getNextBooking());
        assertEquals(1, result.getFirst().getComments().size());
    }

    @Test
    void searchItems_shouldReturnMatchingItems() {
        Item anotherItem = new Item(null, "Перфоратор", "Мощный перфоратор", true, owner, null);
        entityManager.persist(anotherItem);
        entityManager.flush();

        List<ItemDto> result = itemService.searchItems("дрель");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Дрель", result.getFirst().getName());
    }

    @Test
    void addComment_shouldSaveAndReturnComment() {
        Booking booking = new Booking(
                null,
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusDays(1),
                item, booker, BookingStatus.APPROVED
        );
        entityManager.persist(booking);
        entityManager.flush();

        CreateCommentDto createCommentDto = new CreateCommentDto("Отличная вещь!");

        CommentDto result = itemService.addComment(booker.getId(), item.getId(), createCommentDto);

        assertNotNull(result);
        assertEquals("Отличная вещь!", result.getText());
        assertEquals("Booker", result.getAuthorName());
    }
}
