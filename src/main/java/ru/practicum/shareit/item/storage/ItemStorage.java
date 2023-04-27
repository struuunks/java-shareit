package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {
    Item createItem(ItemDto itemDto, Long userId);

    Item updateItem(Long itemId, ItemDto itemDto, Long userId);

    Item viewItemInformation(Long itemId);

    Collection<Item> viewAllItems(Long userId);

    Collection<Item> searchItems(String text);
}
