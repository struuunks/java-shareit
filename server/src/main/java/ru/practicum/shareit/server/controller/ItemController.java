package ru.practicum.shareit.server.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.dto.ItemDtoOwner;
import ru.practicum.shareit.server.item.service.ItemServiceImpl;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/items")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemController {
    final ItemServiceImpl service;

    @PostMapping
    ItemDto createItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Добавление новой вещи пользователем с айди " + userId);
        return service.createItem(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    CommentDto commentItem(@PathVariable Long itemId, @RequestBody CommentDto commentDto,
                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Добавление нового коментария пользователем с айди " + userId + " к вещи с айди " + itemId);
        return service.commentItem(itemId, commentDto, userId);
    }

    @PatchMapping("/{itemId}")
    ItemDto updateItem(@PathVariable Long itemId, @RequestBody ItemDto itemDto,
                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Обновление данных вещи с айди " + itemId);
        return service.updateItem(itemId, itemDto, userId);
    }

    @GetMapping("/{itemId}")
    ItemDtoOwner viewItemInformation(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Просмотр информации о вещи с айди " + itemId);
        return service.getItemById(itemId, userId);
    }

    @GetMapping
    List<ItemDtoOwner> viewAllItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestParam(defaultValue = "0") Integer from,
                                    @RequestParam(defaultValue = "10") Integer size) {
        log.info("Запрошен список всех вещей пользователя с айди " + userId);
        return service.viewAllItems(userId, from, size);
    }

    @GetMapping("/search")
    List<ItemDto> searchItems(@RequestParam String text, @RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestParam(defaultValue = "0") Integer from,
                              @RequestParam(defaultValue = "10") Integer size) {
        log.info("Поиск вещей содержащих '" + text + "' в названии или описании пользователем с айди " + userId);
        return service.searchItems(text, from, size);
    }
}
