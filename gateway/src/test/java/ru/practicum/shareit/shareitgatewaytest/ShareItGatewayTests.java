package ru.practicum.shareit.shareitgatewaytest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.client.BookingClient;
import ru.practicum.shareit.client.ItemClient;
import ru.practicum.shareit.client.ItemRequestClient;
import ru.practicum.shareit.client.UserClient;

@SpringBootTest
@MockBean(UserClient.class)
@MockBean(ItemClient.class)
@MockBean(BookingClient.class)
@MockBean(ItemRequestClient.class)
class ShareItGatewayTests {

    @Test
    void contextLoads() {
    }
}
