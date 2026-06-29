package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ValidationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CreateCommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemDbRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserDbRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


@Service
public class ItemServiceImpl implements ItemService {

    private final ItemDbRepository itemRepository;
    private final UserDbRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemServiceImpl(ItemDbRepository itemRepository,
                           UserDbRepository userRepository,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public ItemDto createItem(Long userId, CreateItemDto createItemDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = ItemMapper.toItem(createItemDto);
        item.setOwner(owner);

        Item savedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, UpdateItemDto updateItemDto) {
        Item oldItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!oldItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Редактировать вещь может только владелец");
        }

        ItemMapper.updateItemFromDto(updateItemDto, oldItem);

        Item updatedItem = itemRepository.save(oldItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(Long userId, Long itemId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        ItemDto itemDto = ItemMapper.toItemDto(item);

        // комментарии добавляем всегда
        List<CommentDto> comments = commentRepository.findAllByItemId(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .toList();
        itemDto.setComments(comments);

        // даты бронирования только для владельца
        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();

            bookingRepository.findFirstByItemIdAndStatusAndEndDateBeforeOrderByEndDateDesc(
                            itemId, BookingStatus.APPROVED, now)
                    .ifPresent(b -> itemDto.setLastBooking(b.getEndDate()));

            bookingRepository.findFirstByItemIdAndStatusAndStartDateAfterOrderByStartDateAsc(
                            itemId, BookingStatus.APPROVED, now)
                    .ifPresent(b -> itemDto.setNextBooking(b.getStartDate()));
        }

        return itemDto;
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }

        List<Item> items = itemRepository.findAllByOwnerId(userId);
        List<Long> itemIds = items.stream().map(Item::getId).toList();
        LocalDateTime now = LocalDateTime.now();

        // достаём все бронирования одним запросом
        List<Booking> allBookings = bookingRepository
                .findAllByItemIdInAndStatus(itemIds, BookingStatus.APPROVED);

        // фильтруем и сортируем уже в памяти
        List<Booking> lastBookings = allBookings.stream()
                .filter(b -> b.getEndDate().isBefore(now))
                .sorted(Comparator.comparing(Booking::getEndDate).reversed())
                .toList();

        List<Booking> nextBookings = allBookings.stream()
                .filter(b -> b.getStartDate().isAfter(now))
                .sorted(Comparator.comparing(Booking::getStartDate))
                .toList();

        List<Comment> comments = commentRepository.findAllByItemIdIn(itemIds);

        return items.stream().map(item -> {
            ItemDto itemDto = ItemMapper.toItemDto(item);

            lastBookings.stream()
                    .filter(b -> b.getItem().getId().equals(item.getId()))
                    .findFirst()
                    .ifPresent(b -> itemDto.setLastBooking(b.getEndDate()));

            nextBookings.stream()
                    .filter(b -> b.getItem().getId().equals(item.getId()))
                    .findFirst()
                    .ifPresent(b -> itemDto.setNextBooking(b.getStartDate()));

            List<CommentDto> itemComments = comments.stream()
                    .filter(c -> c.getItem().getId().equals(item.getId()))
                    .map(CommentMapper::toCommentDto)
                    .toList();
            itemDto.setComments(itemComments);

            return itemDto;
        }).toList();
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CreateCommentDto createCommentDto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndDateBefore(
                itemId, userId, BookingStatus.APPROVED, LocalDateTime.now())) {
            throw new ValidationException("Вы не можете оставить комментарий к этой вещи");
        }

        Comment comment = CommentMapper.toComment(createCommentDto);
        comment.setAuthor(author);
        comment.setItem(item);

        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.toCommentDto(savedComment);
    }
}
