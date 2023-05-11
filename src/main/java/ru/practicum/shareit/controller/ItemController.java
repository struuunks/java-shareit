package ru.practicum.shareit.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOwner;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemController {
    final ItemServiceImpl service;

    @PostMapping
    public ItemDto createItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Добавление новой вещи пользователем с айди " + userId);
        return service.createItem(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto commentItem(@PathVariable Long itemId, @RequestBody CommentDto commentDto,
                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Добавление нового коментария пользователем с айди " + userId + " к вещи с айди " + itemId);
        return service.commentItem(itemId, commentDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId, @RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Обновление данных вещи с айди " + itemId);
        return service.updateItem(itemId, itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoOwner viewItemInformation(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Просмотр информации о вещи с айди " + itemId);
        return service.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoOwner> viewAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрошен список всех вещей пользователя с айди " + userId);
        return service.viewAllItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Поиск вещей содержащих '" + text + "' в названии или описании");
        return service.searchItems(text);
    }
}
