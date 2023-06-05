package ru.practicum.shareit.server.item.service;

import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.dto.ItemDtoOwner;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId);

    ItemDtoOwner getItemById(Long itemId, Long userId);

    CommentDto commentItem(Long itemId, CommentDto comment, Long authorId);

    List<ItemDtoOwner> viewAllItems(Long userId, Integer from, Integer size);

    List<ItemDto> searchItems(String text, Integer from, Integer size);
}
