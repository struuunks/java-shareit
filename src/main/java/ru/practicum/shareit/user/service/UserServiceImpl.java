package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    final InMemoryUserStorage userStorage;

    @Override
    public List<UserDto> getAllUsers() {
        return userStorage.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long userId) {
        if (userStorage.getUserById(userId) == null) {
            throw new DataNotFoundException("Пользователь с айди " + userId + " не найден");
        }
        return UserMapper.toUserDto(userStorage.getUserById(userId));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userStorage.createUser(user));
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        if (userStorage.getUserById(userId) == null) {
            throw new DataNotFoundException("Пользователь с айди " + userId + " не найден");
        }
        return UserMapper.toUserDto(userStorage.updateUser(userId, UserMapper.toUser(userDto)));
    }

    @Override
    public void deleteUser(Long userId) {
        if (userStorage.getUserById(userId) == null) {
            throw new DataNotFoundException("Пользователь с айди " + userId + " не найден");
        }
        userStorage.deleteUser(userId);
    }
}
