package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@SpringBootTest
class UserClientTest {

    @Autowired
    private UserClient userClient;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.bindTo(userClient.getRest()).build();
    }

    @Test
    void getAllUsers_shouldReturnOk() {
        mockServer.expect(requestTo(containsString("/users")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = userClient.getAllUsers();

        assertNotNull(response);
        mockServer.verify();
    }

    @Test
    void createUser_shouldReturnOk() {
        mockServer.expect(requestTo(containsString("/users")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = userClient.createUser(
                new CreateUserDto("User", "user@mail.com"));

        assertNotNull(response);
        mockServer.verify();
    }

    @Test
    void getUserById_shouldReturnOk() {
        mockServer.expect(requestTo(containsString("/users/1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = userClient.getUserById(1L);

        assertNotNull(response);
        mockServer.verify();
    }

    @Test
    void updateUser_shouldReturnOk() {
        mockServer.expect(requestTo(containsString("/users/1")))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = userClient.updateUser(1L,
                new UpdateUserDto("NewName", null));

        assertNotNull(response);
        mockServer.verify();
    }

    @Test
    void deleteUser_shouldReturnOk() {
        mockServer.expect(requestTo(containsString("/users/1")))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess("", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = userClient.deleteUser(1L);

        assertNotNull(response);
        mockServer.verify();
    }

    @Test
    void getUserById_whenServerReturns404_shouldReturn404() {
        mockServer.expect(requestTo(containsString("/users/99")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        ResponseEntity<Object> response = userClient.getUserById(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void getUserById_whenServerReturns500_shouldReturn500() {
        mockServer.expect(requestTo(containsString("/users/1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        ResponseEntity<Object> response = userClient.getUserById(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void getUserById_whenServerReturns404WithBody_shouldReturn404() {
        mockServer.expect(requestTo(containsString("/users/99")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .body("{\"error\":\"not found\"}")
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = userClient.getUserById(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void getUserById_whenServerReturns404WithoutBody_shouldReturn404() {
        mockServer.expect(requestTo(containsString("/users/99")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        ResponseEntity<Object> response = userClient.getUserById(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        mockServer.verify();
    }
}
