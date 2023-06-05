package ru.practicum.shareit.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.gateway.dto.enums.Status;
import ru.practicum.shareit.gateway.exception.InvalidException;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {

    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
    private UserDto booker;
    private Status status;

    public static void validate(BookingDto bookingDto) {
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new InvalidException("Нельзя создать бронь без указания времени начала или окончания");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new InvalidException("Бронь не может закончиться раньше ее начала");
        }
        if (bookingDto.getStart().isBefore(LocalDateTime.now()) || bookingDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new InvalidException("Бронь не может начаться или закончиться раньше текущей даты и времени");
        }
    }
}
