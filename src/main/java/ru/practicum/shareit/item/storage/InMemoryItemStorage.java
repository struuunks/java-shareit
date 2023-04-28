package ru.practicum.shareit.item.storage;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InMemoryItemStorage implements ItemStorage {
    Long id = 0L;
    final Map<Long, Item> items = new HashMap<>();

    public Long generateId() {
        return ++id;
    }

    @Override
    public Item createItem(Item item, User user) {
        item.setOwner(user);
        item.setId(generateId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Long itemId, Item item) {
        if (item.getName() != null) {
            items.get(itemId).setName(item.getName());
        }
        if (item.getDescription() != null) {
            items.get(itemId).setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            items.get(itemId).setAvailable(item.getAvailable());
        }
        return items.get(itemId);
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
