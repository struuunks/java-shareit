package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemValidator;
import ru.practicum.shareit.item.storage.InMemoryItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {
    final InMemoryItemStorage itemStorage;
    final InMemoryUserStorage userStorage;
    final ItemValidator validator;

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        validator.validate(itemDto, userId);
        User user = userStorage.getUserById(userId);
        return ItemMapper.toItemDto(itemStorage.createItem(ItemMapper.toItem(itemDto, user)));
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId) {
        if (itemStorage.getItemById(itemId) == null) {
            throw new DataNotFoundException("Вещь с айди " + itemDto.getId() + " не найдена");
        } else {
            Item item = itemStorage.getItemById(itemId);
            if (!item.getOwner().getId().equals(userId)) {
                throw new DataNotFoundException("Пользователь с айди " + userId + "не является владельцем вещи");
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
            return ItemMapper.toItemDto(itemStorage.updateItem(item));
        }
    }

    @Override
    public ItemDto viewItemInformation(Long itemId) {
        if (itemStorage.getItemById(itemId) == null) {
            throw new DataNotFoundException("Вещь с айди " + itemId + " не найдена");
        }
        return ItemMapper.toItemDto(itemStorage.getItemById(itemId));
    }

    @Override
    public List<ItemDto> viewAllItems(Long userId) {
        return itemStorage.viewAllItems(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        } else {
            return itemStorage.searchItems(text).stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }
}
