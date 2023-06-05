package ru.practicum.shareit.server.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.exception.DataNotFoundException;
import ru.practicum.shareit.server.exception.InvalidException;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.mapper.UserMapper;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserServiceTest {
    @Autowired
    private UserService userService;
    private final User user = new User(null, "user", "user@yandex.ru");

    @Test
    void createUserTest() {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));

        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }

    @Test
    void createUserWithSameEmailTest() {
        userService.createUser(UserMapper.toUserDto(user));
        User user2 = new User(null, "user2", "user@yandex.ru");

        assertThrows(DataIntegrityViolationException.class, () -> userService.createUser(UserMapper.toUserDto(user2)));
    }

    @Test
    void createUserWithInvalidEmailTest() {
        User user2 = new User(null, "user2", "user.yandex.ru");

        assertThrows(InvalidException.class, () -> userService.createUser(UserMapper.toUserDto(user2)));
    }

    @Test
    void updateUserTest() {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        userDto.setName("newName");
        userDto.setEmail("new@email.ru");
        UserDto updateUserDto = userService.updateUser(user.getId(), userDto);

        assertEquals(updateUserDto.getName(), userDto.getName());
        assertEquals(updateUserDto.getEmail(), userDto.getEmail());
    }


    @Test
    void getUserByIdTest() {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        UserDto returnUserDto = userService.getUserById(user.getId());

        assertEquals(returnUserDto.getName(), user.getName());
        assertEquals(returnUserDto.getEmail(), user.getEmail());
    }

    @Test
    void getUserByWrongIdTest() {
        assertThrows(DataNotFoundException.class, () -> userService.getUserById(5L));
    }

    @Test
    void getAllUsersTest() {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        User user2 = new User(null, "user2", "user2@yandex.ru");
        UserDto userDto2 = userService.createUser(UserMapper.toUserDto(user2));

        List<UserDto> userDtoList = userService.getAllUsers();
        assertEquals(userDtoList.size(), 2);
        assertEquals(userDtoList.get(0), userDto);
        assertEquals(userDtoList.get(1), userDto2);
    }

    @Test
    void deleteWrongUserTest() {
        assertThrows(DataNotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void deleteUserTest() {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        List<UserDto> userDtoList = userService.getAllUsers();
        assertEquals(userDtoList.size(), 1);

        userService.deleteUser(user.getId());
        userDtoList = userService.getAllUsers();
        assertEquals(userDtoList.size(), 0);
    }
}
