package ru.practicum.shareit.mappertest;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserMapperTest {

    @Test
    void toUserDto_shouldMapCorrectly() {
        User user = new User(1L, "User", "user@mail.com");

        UserDto dto = UserMapper.toUserDto(user);

        assertEquals(1L, dto.getId());
        assertEquals("User", dto.getName());
        assertEquals("user@mail.com", dto.getEmail());
    }

    @Test
    void toUser_shouldMapCorrectly() {
        CreateUserDto createUserDto = new CreateUserDto("User", "user@mail.com");

        User user = UserMapper.toUser(createUserDto);

        assertNull(user.getId());
        assertEquals("User", user.getName());
        assertEquals("user@mail.com", user.getEmail());
    }

    @Test
    void updateUserFromDto_shouldUpdateOnlyNonNullFields() {
        User user = new User(1L, "OldName", "old@mail.com");
        UpdateUserDto updateUserDto = new UpdateUserDto(null, "new@mail.com");

        UserMapper.updateUserFromDto(updateUserDto, user);

        assertEquals("OldName", user.getName());
        assertEquals("new@mail.com", user.getEmail());
    }
}
