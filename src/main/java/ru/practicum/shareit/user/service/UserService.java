package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

@Service
public interface UserService {
    Collection<User> getAllUsers();

    User getUserById(Long userId);

    User createUser(UserDto userDto);

    User updateUser(Long userId, UserDto userDto);

    void deleteUser(Long userId);
}
