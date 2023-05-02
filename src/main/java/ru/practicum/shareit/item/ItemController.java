package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    ItemServiceImpl service;

    @PostMapping
    public ItemDto createItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Добавлена новая вещь пользователем с айди " + userId);
        return service.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId, @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Обновление данных вещи с айди " + itemId);
        return service.updateItem(itemId, itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto viewItemInformation(@PathVariable long itemId) {
        log.info("Просмотр информации о вещи с айди " + itemId);
        return service.viewItemInformation(itemId);
    }

    @GetMapping
    public List<ItemDto> viewAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрошен список всех вещей пользователя с айди " + userId);
        return service.viewAllItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Поиск вещей содержащих '" + text + "' в названии или описании");
        return service.searchItems(text);
    }
}
