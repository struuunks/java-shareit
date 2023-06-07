package ru.practicum.shareit.gateway.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.client.RequestClient;
import ru.practicum.shareit.gateway.dto.ItemRequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestController {
    final RequestClient client;

    @PostMapping
    ResponseEntity<Object> createRequest(@RequestBody ItemRequestDto itemRequestDto,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Создание запроса на вещь пользователем с айди " + userId);
        return client.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    ResponseEntity<Object> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Просмотр запросов пользователя с айди " + userId);
        return client.getUserRequests(userId);
    }

    @GetMapping("/all")
    ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                          @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Просмотр всех запросов пользователем с айди " + userId);
        return client.getAllRequests(userId, from, size);

    }

    @GetMapping("/{requestId}")
    ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long requestId) {
        log.info("Просмотр запроса с айди" + requestId + " пользователем с айди " + userId);
        return client.getRequestById(userId, requestId);
    }
}
