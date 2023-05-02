package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.InvalidException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;


@Component
@AllArgsConstructor
public class ItemValidator {
    final InMemoryUserStorage userStorage;

    public void validate(ItemDto itemDto, Long userId) {
        if (userId == null) {
            throw new DataNotFoundException("Невозможно создать вещь не указывая ее владельца");
        } else if (userStorage.getUserById(userId) == null) {
            throw new DataNotFoundException("Пользователя с айди " + userId + " не существует");
        }

        if (itemDto.getName() == null || itemDto.getName().isEmpty()) {
            throw new InvalidException("Название вещи не может быть пустым");
        }

        if (itemDto.getDescription() == null || itemDto.getDescription().isEmpty()) {
            throw new InvalidException("Описание вещи не может быть пустым");
        }
        if (itemDto.getAvailable() == null) {
            throw new InvalidException("Необходимо указать доступна ли вещь");
        }
    }
}
