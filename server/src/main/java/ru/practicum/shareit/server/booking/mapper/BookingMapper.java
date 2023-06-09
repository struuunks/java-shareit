package ru.practicum.shareit.server.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.dto.BookingDtoReceived;
import ru.practicum.shareit.server.booking.dto.BookingDtoReturned;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class BookingMapper {

    private static final String TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    public static Booking toBooking(BookingDtoReceived bookingDto, User user, Item item) {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(item)
                .booker(user)
                .status(bookingDto.getStatus())
                .build();
    }

    public static BookingDtoReturned toBookingDtoReturned(Booking booking) {
        return BookingDtoReturned.builder()
                .id(booking.getId())
                .start(LocalDateTime.parse(booking.getStart().format(DateTimeFormatter.ofPattern(TIME_PATTERN))))
                .end(LocalDateTime.parse(booking.getEnd().format(DateTimeFormatter.ofPattern(TIME_PATTERN))))
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(LocalDateTime.parse(booking.getStart().format(DateTimeFormatter.ofPattern(TIME_PATTERN))))
                .end(LocalDateTime.parse(booking.getEnd().format(DateTimeFormatter.ofPattern(TIME_PATTERN))))
                .itemId(booking.getItem().getId())
                .bookerId(booking.getBooker().getId())
                .status(booking.getStatus())
                .build();
    }

    public static BookingDtoReceived toBookingDtoReceived(Booking booking) {
        return BookingDtoReceived.builder()
                .id(booking.getId())
                .start(LocalDateTime.parse(booking.getStart().format(DateTimeFormatter.ofPattern(TIME_PATTERN))))
                .end(LocalDateTime.parse(booking.getEnd().format(DateTimeFormatter.ofPattern(TIME_PATTERN))))
                .itemId(booking.getItem().getId())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }
}
