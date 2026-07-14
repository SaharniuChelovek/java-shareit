package ru.practicum.shareit.client;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.enums.BookingState;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest
class BookingClientTest {

    @Autowired
    private BookingClient bookingClient;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.bindTo(bookingClient.getRest()).build();
    }

    @Test
    void createBooking_shouldReturnOk() {
        mockServer.expect(requestTo(containsString("/bookings")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        CreateBookingDto dto = new CreateBookingDto(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3),
                1L
        );

        ResponseEntity<Object> response = bookingClient.createBooking(1L, dto);

        assertNotNull(response);
        mockServer.verify();
    }

    @Test
    void approveBooking_shouldReturnOk() {
        mockServer.expect(requestTo(containsString("/bookings/1")))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = bookingClient.approveBooking(1L, 1L, true);

        assertNotNull(response);
        mockServer.verify();
    }

    @Test
    void getBookingById_shouldReturnOk() {
        mockServer.expect(requestTo(containsString("/bookings/1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = bookingClient.getBookingById(1L, 1L);

        assertNotNull(response);
        mockServer.verify();
    }

    @Test
    void getBookings_shouldReturnOk() {
        mockServer.expect(requestTo(containsString("/bookings")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = bookingClient.getBookings(1L, BookingState.ALL);

        assertNotNull(response);
        mockServer.verify();
    }

    @Test
    void getOwnerBookings_shouldReturnOk() {
        mockServer.expect(requestTo(containsString("/bookings/owner")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = bookingClient.getOwnerBookings(1L, BookingState.ALL);

        assertNotNull(response);
        mockServer.verify();
    }

    @Test
    void bookingStatus_waiting_shouldReturnOk() {
        mockServer.expect(requestTo(containsString("/bookings")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"id\":1,\"status\":\"WAITING\"}",
                        MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = bookingClient.createBooking(1L,
                new CreateBookingDto(LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(3), 1L));

        assertNotNull(response);
        mockServer.verify();
    }

    @Test
    void bookingStatus_approved_shouldReturnOk() {
        mockServer.expect(requestTo(containsString("/bookings/1")))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withSuccess("{\"id\":1,\"status\":\"APPROVED\"}",
                        MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = bookingClient.approveBooking(1L, 1L, true);

        assertNotNull(response);
        mockServer.verify();
    }

    @Test
    void bookingStatus_rejected_shouldReturnOk() {
        mockServer.expect(requestTo(containsString("/bookings/1")))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withSuccess("{\"id\":1,\"status\":\"REJECTED\"}",
                        MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = bookingClient.approveBooking(1L, 1L, false);

        assertNotNull(response);
        mockServer.verify();
    }
}
