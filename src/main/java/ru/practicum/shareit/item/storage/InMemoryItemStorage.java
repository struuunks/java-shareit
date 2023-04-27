package ru.practicum.shareit.item.storage;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemValidator;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import java.util.*;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InMemoryItemStorage implements ItemStorage {
    Long id = 0L;
    final Map<Long, Item> items = new HashMap<>();

    final InMemoryUserStorage userStorage;
    final ItemValidator validator;

    public Long generateId() {
        return ++id;
    }

    @Override
    public Item createItem(ItemDto itemDto, Long userId) {
        Item item = ItemMapper.toItem(itemDto);
        User user = userStorage.getUserById(userId);
        validator.validate(itemDto, userId);
        item.setOwner(user);
        item.setId(generateId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Long itemId, ItemDto itemDto, Long userId) {
        Item item = items.get(itemId);
        if (!items.containsKey(itemId)) {
            throw new IdNotFoundException("Вещь с айди " + itemDto.getId() + " не найдена");
        } else {
            if (!item.getOwner().getId().equals(userId)) {
                throw new IdNotFoundException("Пользователь с айди " + itemId + "не является владельцем вещи");
            }
            if (itemDto.getName() != null) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
            }
            item.setId(itemId);
            items.put(itemId, item);
        }
        return item;
    }

    @Override
    public Item viewItemInformation(Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new IdNotFoundException("Вещь с айди " + itemId + " не найдена");
        } else {
            return items.get(itemId);
        }
    }

    @Override
    public Collection<Item> viewAllItems(Long userId) {
        Collection<Item> allItems = new ArrayList<>(items.values());
        Collection<Item> itemsByUser = new ArrayList<>();
        for (Item i : allItems) {
            if (i.getOwner().getId().equals(userId)) {
                itemsByUser.add(i);
            }
        }
        return itemsByUser;
    }

    @Override
    public Collection<Item> searchItems(String text) {
        Collection<Item> searchedItems = new ArrayList<>();
        if (text.isBlank()) {
            return Collections.emptyList();
        } else {
            List<Item> itemsList = new ArrayList<>(items.values());
            for (Item i : itemsList) {
                if (i.getDescription().toLowerCase().contains(text.toLowerCase()) && i.getAvailable()) {
                    searchedItems.add(i);
                } else if (i.getName().toLowerCase().contains(text.toLowerCase()) && i.getAvailable()) {
                    searchedItems.add(i);
                }
            }
            return searchedItems;
        }
    }
}
