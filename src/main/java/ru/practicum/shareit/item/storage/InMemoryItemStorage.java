package ru.practicum.shareit.item.storage;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InMemoryItemStorage implements ItemStorage {
    Long id = 0L;
    final Map<Long, Item> items = new HashMap<>();

    public Long generateId() {
        return ++id;
    }

    @Override
    public Item createItem(Item item) {
        item.setId(generateId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public Item getItemById(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> viewAllItems(Long userId) {
        return items.values().stream()
                .filter(i -> i.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItems(String text) {
        return items.values().stream()
                .filter(i -> (i.getName().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))
                        || i.getDescription().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))
                        && i.getAvailable()))
                .collect(Collectors.toList());
    }
}
