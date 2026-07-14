package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(CreateUserDto createUserDto);

    UserDto updateUser(Long userId, UpdateUserDto updateUserDto);

    UserDto getUser(Long userId);

    List<UserDto> getAllUsers();

    void deleteUser(Long userId);

}
