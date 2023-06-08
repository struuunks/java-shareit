package ru.practicum.shareit.server.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.service.UserServiceImpl;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserController {
    final UserServiceImpl service;

    @PostMapping
    UserDto createUser(@RequestBody UserDto userDto) {
        log.info("Добавление нового пользователя");
        return service.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    UserDto updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("Обновление данных пользователя с айди " + userId);
        return service.updateUser(userId, userDto);
    }

    @GetMapping("/{userId}")
    UserDto getUserById(@PathVariable Long userId) {
        log.info("Запрошен пользователь с айди " + userId);
        return service.getUserById(userId);
    }

    @GetMapping
    List<UserDto> getAllUsers() {
        log.info("Запрошены все пользователи");
        return service.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    void deleteUser(@PathVariable Long userId) {
        log.info("Удаление пользователь с айди " + userId);
        service.deleteUser(userId);
    }
}
