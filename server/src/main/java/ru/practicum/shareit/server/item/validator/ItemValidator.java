package ru.practicum.shareit.server.item.validator;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.server.exception.DataNotFoundException;
import ru.practicum.shareit.server.exception.InvalidException;
import ru.practicum.shareit.server.item.dto.ItemDto;

@Component
public class ItemValidator {

    public void validate(ItemDto itemDto, Long userId) {
        if (userId == null) {
            throw new DataNotFoundException("Невозможно создать вещь не указывая ее владельца");
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
