package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

@Component
public interface UserStorage {
    Collection<User> getAllUsers();

    User getUserById(Long id);

    User createUser(UserDto userDto);

    User updateUser(Long userId, UserDto userDto);

    void deleteUser(Long userId);
}
