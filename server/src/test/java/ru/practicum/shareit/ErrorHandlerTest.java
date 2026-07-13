package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.error.exception.ConflictException;
import ru.practicum.shareit.error.exception.ForbiddenException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ValidationException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.service.UserService;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class ErrorHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void shouldReturn404_whenNotFoundException() throws Exception {
        when(userService.getUser(anyLong()))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Пользователь не найден"));
    }

    @Test
    void shouldReturn400_whenValidationException() throws Exception {
        when(userService.getUser(anyLong()))
                .thenThrow(new ValidationException("Ошибка валидации"));

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ошибка валидации"));
    }

    @Test
    void shouldReturn409_whenConflictException() throws Exception {
        when(userService.getUser(anyLong()))
                .thenThrow(new ConflictException("Конфликт"));

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Конфликт"));
    }

    @Test
    void shouldReturn403_whenForbiddenException() throws Exception {
        when(userService.getUser(anyLong()))
                .thenThrow(new ForbiddenException("Доступ запрещён"));

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Доступ запрещён"));
    }

    @Test
    void shouldReturn500_whenUnexpectedException() throws Exception {
        when(userService.getUser(anyLong()))
                .thenThrow(new RuntimeException("Неожиданная ошибка"));

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Произошла ошибка сервера"));
    }

    @Test
    void shouldReturn400_whenMethodArgumentNotValid() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"email\":\"user@mail.com\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}
