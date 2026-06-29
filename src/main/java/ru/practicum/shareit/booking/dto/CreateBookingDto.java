package ru.practicum.shareit.booking.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookingDto {

    @NotNull(message = "Дата начала не может быть пустой")
    @JsonProperty("start")
    private LocalDateTime startDate;

    @NotNull(message = "Дата окончания не может быть пустой")
    @JsonProperty("end")
    private LocalDateTime endDate;

    @NotNull(message = "Вещь не может быть пустой")
    private Long itemId;
}
