package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOwner;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId);

    ItemDtoOwner getItemById(Long itemId, Long userId);

    CommentDto commentItem(Long itemId, CommentDto comment, Long authorId);

    List<ItemDtoOwner> viewAllItems(Long userId);

    List<ItemDto> searchItems(String text);
}
