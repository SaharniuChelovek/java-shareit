package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.ConflictException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserDbRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserDbRepository userRepository;

    public UserServiceImpl(final UserDbRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto createUser(CreateUserDto createUserDto) {

        User existingUser = userRepository.findByEmail(createUserDto.getEmail());
        if (existingUser != null) {

            throw new ConflictException("Пользователь с таким email уже существует");
        }

        User user = UserMapper.toUser(createUserDto);

        User savedUser = userRepository.save(user);

        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(Long userId, UpdateUserDto updateUserDto) {

        User oldUser = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (updateUserDto.getEmail() != null) {
            User existingUser = userRepository.findByEmail(updateUserDto.getEmail());
            if (existingUser != null && !existingUser.getId().equals(userId)) {
                throw new ConflictException("Этот email уже занят");
            }
        }

        UserMapper.updateUserFromDto(updateUserDto, oldUser);

        User updatedUser = userRepository.save(oldUser);

        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public UserDto getUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {

        Collection<User> users = userRepository.findAll();

        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        userRepository.deleteById(userId);
    }
}
