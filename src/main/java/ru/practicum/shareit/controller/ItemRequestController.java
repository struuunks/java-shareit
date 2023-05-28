package ru.practicum.shareit.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestServiceImpl;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestController {
    final RequestServiceImpl service;

    @PostMapping
    ItemRequestDto createRequest(@RequestBody ItemRequestDto itemRequestDto,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Создание запроса на вещь пользователем с айди " + userId);
        return service.createRequest(itemRequestDto, userId);
    }

    @GetMapping
    List<ItemRequestDto> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Просмотр запросов пользователя с айди " + userId);
        return service.getUserRequests(userId);
    }

    @GetMapping("/all")
    List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                        @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Просмотр всех запросов пользователем с айди " + userId);
        return service.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
        log.info("Просмотр запроса с айди" + requestId + " пользователем с айди " + userId);
        return service.getRequestById(userId, requestId);
    }
}
