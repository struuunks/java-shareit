package ru.practicum.shareit.gateway.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.client.UserClient;
import ru.practicum.shareit.gateway.dto.UserDto;
import ru.practicum.shareit.gateway.dto.Validated.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserController {
    final UserClient userClient;

    @PostMapping
    ResponseEntity<Object> createUser(@RequestBody @Validated(Create.class) UserDto userDto) {
        log.info("Добавление нового пользователя");
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    ResponseEntity<Object> updateUser(@PathVariable Long userId, @RequestBody @Validated(Update.class) UserDto userDto) {
        log.info("Обновление данных пользователя с айди " + userId);
        return userClient.updateUser(userId, userDto);
    }

    @GetMapping("/{userId}")
    ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        log.info("Запрошен пользователь с айди " + userId);
        return userClient.getUserById(userId);
    }

    @GetMapping
    ResponseEntity<Object> getAllUsers() {
        log.info("Запрошены все пользователи");
        return userClient.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    void deleteUser(@PathVariable Long userId) {
        log.info("Удаление пользователь с айди " + userId);
        userClient.deleteUser(userId);
    }
}
