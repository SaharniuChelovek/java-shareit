package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.exception.ConflictException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser() {

        UserDto userDto = new UserDto(null, "Ivan", "ivan@mail.ru");
        User savedUser = new User(1L, "Ivan", "ivan@mail.ru");

        when(userRepository.findByEmail("ivan@mail.ru")).thenReturn(null);

        when(userRepository.create(any(User.class))).thenReturn(savedUser);

        UserDto result = userService.createUser(userDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("ivan@mail.ru", result.getEmail());

        verify(userRepository, times(1)).create(any(User.class));
    }

    @Test
    void createUserShouldThrowConflict() {

        UserDto userDto = new UserDto(null, "Ivan", "ivan@mail.ru");
        User user = new User(2L, "Petr", "ivan@mail.ru");

        when(userRepository.findByEmail("ivan@mail.ru")).thenReturn(user);

        assertThrows(ConflictException.class, () -> userService.createUser(userDto));

        verify(userRepository, never()).create(any(User.class));
    }

    @Test
    void updateUserWhenUserNotExists() {

        Long userId = 999L;
        UserDto userDto = new UserDto(null, "NewName", "new@mail.ru");

        when(userRepository.getUserById(userId)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> userService.updateUser(userId, userDto));
    }

}