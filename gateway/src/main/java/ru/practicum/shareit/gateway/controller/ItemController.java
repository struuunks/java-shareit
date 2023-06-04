package ru.practicum.shareit.gateway.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.client.ItemClient;
import ru.practicum.shareit.gateway.dto.ItemDto;
import ru.practicum.shareit.gateway.dto.CommentDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/items")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemController {

    final ItemClient client;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestBody ItemDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.createItem(userId, itemDto);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> commentItem(@PathVariable Long itemId, @RequestBody CommentDto commentDto,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.commentItem(userId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable Long itemId, @RequestBody ItemDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Long itemId,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> viewAllItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                               @RequestParam(defaultValue = "10") @Positive Integer size) {
        return client.viewAllItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text, @RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(defaultValue = "0") Integer from,
                                              @RequestParam(defaultValue = "10") Integer size) {
        return client.searchItems(text, userId, from, size);
    }
}
