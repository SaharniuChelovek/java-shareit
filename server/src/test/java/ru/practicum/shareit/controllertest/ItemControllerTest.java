package ru.practicum.shareit.controllertest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getItemById_shouldReturn200() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "Дрель", "Мощная дрель", true, 1L, null, null, null, new ArrayList<>());

        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель"));
    }

    @Test
    void createItem_shouldReturn200() throws Exception {
        CreateItemDto createItemDto = new CreateItemDto("Дрель", "Мощная дрель", true, null);
        ItemDto itemDto = new ItemDto(1L, "Дрель", "Мощная дрель", true, 1L, null, null, null, new ArrayList<>());

        when(itemService.createItem(anyLong(), any())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель"));
    }

    @Test
    void updateItem_shouldReturn200() throws Exception {
        UpdateItemDto updateItemDto = new UpdateItemDto("Новое название", null, null);
        ItemDto itemDto = new ItemDto(1L, "Новое название", "Мощная дрель", true, 1L, null, null, null, new ArrayList<>());

        when(itemService.updateItem(anyLong(), anyLong(), any())).thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Новое название"));
    }


    @Test
    void getItemsByOwner_shouldReturn200() throws Exception {
        List<ItemDto> items = List.of(
                new ItemDto(1L, "Дрель", "Мощная дрель", true, 1L, null, null, null, new ArrayList<>())
        );

        when(itemService.getItemsByOwner(anyLong())).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Дрель"));
    }

    @Test
    void searchItems_shouldReturn200() throws Exception {
        List<ItemDto> items = List.of(
                new ItemDto(1L, "Дрель", "Мощная дрель", true, 1L, null, null, null, new ArrayList<>())
        );

        when(itemService.searchItems(anyString())).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", "дрель"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Дрель"));
    }

    @Test
    void addComment_shouldReturn200() throws Exception {
        CreateCommentDto createCommentDto = new CreateCommentDto("Отличная вещь!");
        CommentDto commentDto = new CommentDto(1L, "Отличная вещь!", "Booker", LocalDateTime.now());

        when(itemService.addComment(anyLong(), anyLong(), any())).thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCommentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Отличная вещь!"))
                .andExpect(jsonPath("$.authorName").value("Booker"));
    }


}


