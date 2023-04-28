package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId);

    ItemDto viewItemInformation(Long itemId);

    List<ItemDto> viewAllItems(Long userId);

    List<ItemDto> searchItems(String text);
}
