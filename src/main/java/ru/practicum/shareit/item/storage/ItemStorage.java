package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemStorage {
    Item createItem(Item item, User user);

    Item updateItem(Long itemId, Item item);

    Item getItemById(Long itemId);

    List<Item> viewAllItems(Long userId);

    List<Item> searchItems(String text);
}
