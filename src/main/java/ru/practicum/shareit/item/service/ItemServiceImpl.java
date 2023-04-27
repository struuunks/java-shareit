package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.InMemoryItemStorage;

import java.util.Collection;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    final InMemoryItemStorage itemStorage;

    @Override
    public Item createItem(ItemDto itemDto, Long userId) {
        return itemStorage.createItem(itemDto, userId);
    }

    @Override
    public Item updateItem(Long itemId, ItemDto itemDto, Long userId) {
        return itemStorage.updateItem(itemId, itemDto, userId);
    }

    @Override
    public Item viewItemInformation(Long itemId) {
        return itemStorage.viewItemInformation(itemId);
    }

    @Override
    public Collection<Item> viewAllItems(Long userId) {
        return itemStorage.viewAllItems(userId);
    }

    @Override
    public Collection<Item> searchItems(String text) {
        return itemStorage.searchItems(text);
    }
}
