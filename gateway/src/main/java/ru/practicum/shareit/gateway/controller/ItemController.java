package ru.practicum.shareit.gateway.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.client.ItemClient;
import ru.practicum.shareit.gateway.dto.ItemDto;
import ru.practicum.shareit.gateway.dto.CommentDto;
import ru.practicum.shareit.gateway.dto.Validated.*;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/items")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemController {

    final ItemClient client;

    @PostMapping
    ResponseEntity<Object> createItem(@RequestBody @Validated(Create.class) ItemDto itemDto,
                                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Добавление новой вещи пользователем с айди " + userId);
        return client.createItem(userId, itemDto);
    }

    @PostMapping("{itemId}/comment")
    ResponseEntity<Object> commentItem(@PathVariable Long itemId, @RequestBody CommentDto commentDto,
                                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Добавление нового коментария пользователем с айди " + userId + " к вещи с айди " + itemId);
        return client.commentItem(userId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    ResponseEntity<Object> updateItem(@PathVariable Long itemId, @RequestBody @Validated(Update.class) ItemDto itemDto,
                                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Обновление данных вещи с айди " + itemId);
        return client.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    ResponseEntity<Object> getItemById(@PathVariable Long itemId,
                                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Просмотр информации о вещи с айди " + itemId);
        return client.getItemById(userId, itemId);
    }

    @GetMapping
    ResponseEntity<Object> viewAllItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                        @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Запрошен список всех вещей пользователя с айди " + userId + " , from={}, size={}", from, size);
        return client.viewAllItems(userId, from, size);
    }

    @GetMapping("/search")
    ResponseEntity<Object> searchItems(@RequestParam String text, @RequestHeader("X-Sharer-User-Id") Long userId,
                                       @RequestParam(defaultValue = "0") Integer from,
                                       @RequestParam(defaultValue = "10") Integer size) {
        log.info("Поиск вещей содержащих '" + text + "' в названии или описании пользователем с айди " + userId
                + " , from={}, size={}", from, size);
        return client.searchItems(text, userId, from, size);
    }
}
