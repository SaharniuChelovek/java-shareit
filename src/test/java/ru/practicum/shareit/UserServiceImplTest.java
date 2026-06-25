package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.exception.ConflictException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserDbRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDbRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser() {

        CreateUserDto createDto = new CreateUserDto();
        createDto.setName("Ivan");
        createDto.setEmail("ivan@mail.ru");

        User savedUser = new User(1L, "Ivan", "ivan@mail.ru");

        when(userRepository.findByEmail("ivan@mail.ru")).thenReturn(null);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDto result = userService.createUser(createDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("ivan@mail.ru", result.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUserShouldThrowConflict() {

        CreateUserDto updateUserDto = new CreateUserDto("Ivan", "ivan@mail.ru");
        User user = new User(2L, "Petr", "ivan@mail.ru");

        when(userRepository.findByEmail("ivan@mail.ru")).thenReturn(user);

        assertThrows(ConflictException.class, () -> userService.createUser(updateUserDto));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserWhenUserNotExists() {

        Long userId = 999L;
        UpdateUserDto updateUserDto = new UpdateUserDto("NewName", "new@mail.ru");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(userId, updateUserDto));
    }

}