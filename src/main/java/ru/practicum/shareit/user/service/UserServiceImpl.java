package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import java.util.Collection;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    final InMemoryUserStorage userStorage;

    @Override
    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public User getUserById(Long userId) {
        return userStorage.getUserById(userId);
    }

    @Override
    public User createUser(UserDto userDto) {
        return userStorage.createUser(userDto);
    }

    @Override
    public User updateUser(Long userId, UserDto userDto) {
        return userStorage.updateUser(userId, userDto);
    }

    @Override
    public void deleteUser(Long userId) {
        userStorage.deleteUser(userId);
    }
}
