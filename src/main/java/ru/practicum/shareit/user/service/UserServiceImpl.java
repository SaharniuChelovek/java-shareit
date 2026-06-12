package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.ConflictException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto createUser(UserDto userDto) {

        User existingUser = userRepository.findByEmail(userDto.getEmail());
        if (existingUser != null) {

            throw new ConflictException("Пользователь с таким email уже существует");
        }

        User user = UserMapper.toUser(userDto);

        User savedUser = userRepository.create(user);

        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {

        User oldUser = userRepository.getUserById(userId);
        if (oldUser == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        if (userDto.getEmail() != null) {
            User existingUser = userRepository.findByEmail(userDto.getEmail());
            if (existingUser != null && !existingUser.getId().equals(userId)) {
                throw new ConflictException("Этот email уже занят");
            }
        }

        if (userDto.getName() != null) {
            oldUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            oldUser.setEmail(userDto.getEmail());
        }

        User updatedUser = userRepository.update(oldUser);

        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public UserDto getUser(Long userId) {

        User user = userRepository.getUserById(userId);

        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }
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
        if (userRepository.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        userRepository.delete(userId);
    }
}
