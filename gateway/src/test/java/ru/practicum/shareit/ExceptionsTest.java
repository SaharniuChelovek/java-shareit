package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.error.exception.ConflictException;
import ru.practicum.shareit.error.exception.ForbiddenException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExceptionsTest {

    @Test
    void conflictException_shouldHaveMessage() {
        ConflictException exception = new ConflictException("Конфликт");
        assertEquals("Конфликт", exception.getMessage());
    }

    @Test
    void forbiddenException_shouldHaveMessage() {
        ForbiddenException exception = new ForbiddenException("Доступ запрещён");
        assertEquals("Доступ запрещён", exception.getMessage());
    }

    @Test
    void notFoundException_shouldHaveMessage() {
        NotFoundException exception = new NotFoundException("Не найдено");
        assertEquals("Не найдено", exception.getMessage());
    }

    @Test
    void validationException_shouldHaveMessage() {
        ValidationException exception = new ValidationException("Ошибка валидации");
        assertEquals("Ошибка валидации", exception.getMessage());
    }
}
