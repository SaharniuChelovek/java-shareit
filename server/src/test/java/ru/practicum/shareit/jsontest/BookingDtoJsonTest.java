package ru.practicum.shareit.jsontest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testSerialize() throws Exception {
        UserDto booker = new UserDto(1L, "Booker", "booker@mail.ru");
        ItemDto item = new ItemDto(1L, "Дрель", "Мощная дрель", true, 2L, null, null, null, new ArrayList<>());
        BookingDto dto = new BookingDto(
                1L,
                LocalDateTime.of(2024, 1, 1, 10, 0, 0),
                LocalDateTime.of(2024, 1, 2, 10, 0, 0),
                item,
                booker,
                BookingStatus.WAITING
        );

        assertThat(json.write(dto))
                .extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json.write(dto))
                .extractingJsonPathStringValue("$.start").isEqualTo("2024-01-01T10:00:00");
        assertThat(json.write(dto))
                .extractingJsonPathStringValue("$.end").isEqualTo("2024-01-02T10:00:00");
        assertThat(json.write(dto))
                .extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
        assertThat(json.write(dto))
                .extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(json.write(dto))
                .extractingJsonPathStringValue("$.item.name").isEqualTo("Дрель");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{" +
                "\"id\":1," +
                "\"start\":\"2024-01-01T10:00:00\"," +
                "\"end\":\"2024-01-02T10:00:00\"," +
                "\"status\":\"WAITING\"" +
                "}";

        BookingDto dto = json.parse(content).getObject();

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(LocalDateTime.of(2024, 1, 1, 10, 0, 0), dto.getStartDate());
        assertEquals(LocalDateTime.of(2024, 1, 2, 10, 0, 0), dto.getEndDate());
        assertEquals(BookingStatus.WAITING, dto.getStatus());
    }
}