package ru.practicum.shareit.server.booking.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.server.booking.model.Status;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDtoReceived {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    Long itemId;
    User booker;
    Status status;
}
