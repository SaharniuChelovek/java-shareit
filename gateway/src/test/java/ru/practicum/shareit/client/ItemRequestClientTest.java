package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest
class ItemRequestClientTest {

    @Autowired
    private ItemRequestClient itemRequestClient;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.bindTo(itemRequestClient.getRest()).build();
    }

    @Test
    void createRequest_shouldReturnOk() {
        mockServer.expect(requestTo(containsString("/requests")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        CreateItemRequestDto dto = new CreateItemRequestDto("Нужна дрель");

        ResponseEntity<Object> response = itemRequestClient.createRequest(1L, dto);

        assertNotNull(response);
        mockServer.verify();
    }

    @Test
    void getOwnRequests_shouldReturnOk() {
        mockServer.expect(requestTo(containsString("/requests")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemRequestClient.getOwnRequests(1L);

        assertNotNull(response);
        mockServer.verify();
    }

    @Test
    void getAllRequests_shouldReturnOk() {
        mockServer.expect(requestTo(containsString("/requests/all")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemRequestClient.getAllRequests(1L);

        assertNotNull(response);
        mockServer.verify();
    }

    @Test
    void getRequestById_shouldReturnOk() {
        mockServer.expect(requestTo(containsString("/requests/1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemRequestClient.getRequestById(1L, 1L);

        assertNotNull(response);
        mockServer.verify();
    }
}
