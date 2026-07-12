package ru.practicum.shareit.jsontest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@JsonTest
class CreateBookingDtoJsonTest {

    @Autowired
    private JacksonTester<CreateBookingDto> json;

    @Test
    void testSerialize() throws Exception {
        CreateBookingDto dto = new CreateBookingDto(
                LocalDateTime.of(2024, 1, 1, 10, 0, 0),
                LocalDateTime.of(2024, 1, 2, 10, 0, 0),
                1L
        );

        assertThat(json.write(dto))
                .extractingJsonPathStringValue("$.start").isEqualTo("2024-01-01T10:00:00");
        assertThat(json.write(dto))
                .extractingJsonPathStringValue("$.end").isEqualTo("2024-01-02T10:00:00");
        assertThat(json.write(dto))
                .extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{" +
                "\"start\":\"2024-01-01T10:00:00\"," +
                "\"end\":\"2024-01-02T10:00:00\"," +
                "\"itemId\":1" +
                "}";

        CreateBookingDto dto = json.parse(content).getObject();

        assertNotNull(dto);
        assertEquals(LocalDateTime.of(2024, 1, 1, 10, 0, 0), dto.getStartDate());
        assertEquals(LocalDateTime.of(2024, 1, 2, 10, 0, 0), dto.getEndDate());
        assertEquals(1L, dto.getItemId());
    }

    @Test
    void testDeserializeWithNullStart() throws Exception {
        String content = "{" +
                "\"end\":\"2024-01-02T10:00:00\"," +
                "\"itemId\":1" +
                "}";

        CreateBookingDto dto = json.parse(content).getObject();

        assertNull(dto.getStartDate());
    }
}