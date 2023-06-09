package ru.practicum.shareit.server.booking.service;

import ru.practicum.shareit.server.booking.dto.BookingDtoReceived;
import ru.practicum.shareit.server.booking.dto.BookingDtoReturned;

import java.util.List;

public interface BookingService {
    BookingDtoReturned createBooking(BookingDtoReceived bookingDto, Long userId);

    BookingDtoReturned bookingConfirmation(Long bookingId, Boolean confirmation, Long userId);

    BookingDtoReturned getBookingById(Long bookingId, Long userId);

    List<BookingDtoReturned> getAllBookingsByUser(String state, Long userId, Integer from, Integer size);

    List<BookingDtoReturned> getAllBookingsByOwner(String state, Long ownerId, Integer from, Integer size);
}
