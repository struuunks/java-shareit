package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDtoOwner {
    Long id;
    String name;
    String description;
    Boolean available;
    ItemRequest request;
    BookingDto lastBooking;
    BookingDto nextBooking;
    List<CommentDto> comments;
}
