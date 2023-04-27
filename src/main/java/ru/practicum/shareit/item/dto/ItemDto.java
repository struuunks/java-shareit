package ru.practicum.shareit.item.dto;


import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.model.User;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    Long id;
    String name;
    String description;
    Boolean available;
    User owner;
    Long requestDtoId;
}
