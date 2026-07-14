package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.item.comment.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest
class ItemClientTest {

    @Autowired
    private ItemClient itemClient;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.bindTo(itemClient.getRest()).build();
    }

    @Test
    void createItem_shouldReturnOk() {
        mockServer.expect(requestTo(containsString("/items")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        CreateItemDto dto = new CreateItemDto("Дрель", "Мощная дрель", true, null);

        ResponseEntity<Object> response = itemClient.createItem(1L, dto);

        assertNotNull(response);
        mockServer.verify();
    }

    @Test
    void updateItem_shouldReturnOk() {
        mockServer.expect(requestTo(containsString("/items/1")))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        UpdateItemDto dto = new UpdateItemDto("Новое название", null, null);

        ResponseEntity<Object> response = itemClient.updateItem(1L, 1L, dto);

        assertNotNull(response);
        mockServer.verify();
    }

    @Test
    void getItemById_shouldReturnOk() {
        mockServer.expect(requestTo(containsString("/items/1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemClient.getItemById(1L, 1L);

        assertNotNull(response);
        mockServer.verify();
    }

    @Test
    void getItemsByOwner_shouldReturnOk() {
        mockServer.expect(requestTo(containsString("/items")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemClient.getItemsByOwner(1L);

        assertNotNull(response);
        mockServer.verify();
    }

    @Test
    void searchItems_shouldReturnOk() {
        mockServer.expect(requestTo(containsString("/items/search")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemClient.searchItems("дрель");

        assertNotNull(response);
        mockServer.verify();
    }

    @Test
    void addComment_shouldReturnOk() {
        mockServer.expect(requestTo(containsString("/items/1/comment")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        CreateCommentDto dto = new CreateCommentDto("Отличная вещь!");

        ResponseEntity<Object> response = itemClient.addComment(1L, 1L, dto);

        assertNotNull(response);
        mockServer.verify();
    }
}
