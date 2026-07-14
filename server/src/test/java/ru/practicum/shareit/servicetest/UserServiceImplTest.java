package ru.practicum.shareit.servicetest;

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
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
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

    // updateUser success
    @Test
    void updateUserSuccess() {
        User oldUser = new User(1L, "Ivan", "ivan@mail.ru");
        User updatedUser = new User(1L, "NewName", "new@mail.ru");
        UpdateUserDto updateUserDto = new UpdateUserDto("NewName", "new@mail.ru");

        when(userRepository.findById(1L)).thenReturn(Optional.of(oldUser));
        when(userRepository.findByEmail("new@mail.ru")).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto result = userService.updateUser(1L, updateUserDto);

        assertNotNull(result);
        assertEquals("NewName", result.getName());
        assertEquals("new@mail.ru", result.getEmail());
    }

    // updateUser email conflict
    @Test
    void updateUserWhenEmailConflict() {
        User oldUser = new User(1L, "Ivan", "ivan@mail.ru");
        User otherUser = new User(2L, "Petr", "new@mail.ru");
        UpdateUserDto updateUserDto = new UpdateUserDto(null, "new@mail.ru");

        when(userRepository.findById(1L)).thenReturn(Optional.of(oldUser));
        when(userRepository.findByEmail("new@mail.ru")).thenReturn(otherUser);

        assertThrows(ConflictException.class, () -> userService.updateUser(1L, updateUserDto));
    }

    // getUser
    @Test
    void getUserWhenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUser(99L));
    }

    @Test
    void getUserSuccess() {
        User user = new User(1L, "Ivan", "ivan@mail.ru");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.getUser(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Ivan", result.getName());
    }

    // getAllUsers
    @Test
    void getAllUsersSuccess() {
        when(userRepository.findAll()).thenReturn(List.of(
                new User(1L, "Ivan", "ivan@mail.ru"),
                new User(2L, "Petr", "petr@mail.ru")
        ));

        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    // deleteUser
    @Test
    void deleteUserWhenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.deleteUser(99L));
    }

    @Test
    void deleteUserSuccess() {
        User user = new User(1L, "Ivan", "ivan@mail.ru");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

}