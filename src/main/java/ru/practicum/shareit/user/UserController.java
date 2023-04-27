package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {
    private final UserServiceImpl service;

    @PostMapping
    public User createUser(@RequestBody UserDto userDto) {
        log.info("Добавлен новый пользователь");
        return service.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public User updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("Обновление данных пользователя с айди " + userId);
        return service.updateUser(userId, userDto);
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable Long userId) {
        log.info("Запрошен пользователь с айди " + userId);
        return service.getUserById(userId);
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Запрошены все пользователи");
        return service.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Удален пользователь с айди " + userId);
        service.deleteUser(userId);
    }
}
