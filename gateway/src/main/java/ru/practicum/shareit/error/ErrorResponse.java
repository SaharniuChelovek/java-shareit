package ru.practicum.shareit.error;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

}
