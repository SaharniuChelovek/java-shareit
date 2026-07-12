package ru.practicum.shareit.integrationtest;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemDbRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserDbRepository;

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
    private UserDbRepository userRepository;

    @Autowired
    private ItemDbRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(new User(null, "Owner", "owner@mail.ru"));
        booker = userRepository.save(new User(null, "Booker", "booker@mail.ru"));
        item = itemRepository.save(new Item(null, "Дрель", "Мощная дрель", true, owner, null));
    }

    @Test
    void getItemsByOwner_shouldReturnItemsWithBookingsAndComments() {
        Booking pastBooking = bookingRepository.save(new Booking(
                null,
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusDays(1),
                item,
                booker,
                BookingStatus.APPROVED
        ));

        Booking futureBooking = bookingRepository.save(new Booking(
                null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3),
                item,
                booker,
                BookingStatus.APPROVED
        ));

        commentRepository.save(new Comment(
                null,
                "Отличная дрель!",
                item,
                booker,
                LocalDateTime.now()
        ));

        List<ItemDto> result = itemService.getItemsByOwner(owner.getId());

        assertNotNull(result);
        assertEquals(1, result.size());

        ItemDto itemDto = result.getFirst();
        assertEquals("Дрель", itemDto.getName());
        assertEquals(pastBooking.getEndDate(), itemDto.getLastBooking());
        assertEquals(futureBooking.getStartDate(), itemDto.getNextBooking());
        assertEquals(1, itemDto.getComments().size());
        assertEquals("Отличная дрель!", itemDto.getComments().getFirst().getText());
    }

    @Test
    void searchItems_shouldReturnMatchingItems() {
        itemRepository.save(new Item(null, "Перфоратор", "Мощный перфоратор", true, owner, null));

        List<ItemDto> result = itemService.searchItems("дрель");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Дрель", result.getFirst().getName());
    }

    @Test
    void addComment_shouldSaveAndReturnComment() {
        bookingRepository.save(new Booking(
                null,
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusDays(1),
                item,
                booker,
                BookingStatus.APPROVED
        ));

        CreateCommentDto createCommentDto = new CreateCommentDto("Отличная вещь!");

        CommentDto result = itemService.addComment(booker.getId(), item.getId(), createCommentDto);

        assertNotNull(result);
        assertEquals("Отличная вещь!", result.getText());
        assertEquals("Booker", result.getAuthorName());
    }
}
