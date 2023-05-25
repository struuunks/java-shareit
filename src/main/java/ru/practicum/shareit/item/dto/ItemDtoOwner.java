package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDtoOwner {
    Long id;
    String name;
    String description;
    Boolean available;
    Long requestId;
    BookingDto lastBooking;
    BookingDto nextBooking;
    List<CommentDto> comments;
}
