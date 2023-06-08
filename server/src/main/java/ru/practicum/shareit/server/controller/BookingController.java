package ru.practicum.shareit.server.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.booking.dto.BookingDtoReceived;
import ru.practicum.shareit.server.booking.dto.BookingDtoReturned;
import ru.practicum.shareit.server.booking.service.BookingServiceImpl;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingController {
    final BookingServiceImpl bookingService;

    @PostMapping
    BookingDtoReturned createBooking(@RequestBody BookingDtoReceived bookingDto,
                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Создание нового бронирования");
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    BookingDtoReturned bookingConfirmation(@PathVariable Long bookingId, @RequestParam Boolean approved,
                                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Подтверждение бронирования с айди " + bookingId + " владельцем вещи");
        return bookingService.bookingConfirmation(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    BookingDtoReturned getBookingById(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Просмотр бронирования с айди " + bookingId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    List<BookingDtoReturned> getAllBookingsByUser(@RequestParam(defaultValue = "ALL") String state,
                                                  @RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(defaultValue = "0") Integer from,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        log.info("Просмотр бронирований пользователем с айди " + userId);
        return bookingService.getAllBookingsByUser(state, userId, from, size);
    }

    @GetMapping("/owner")
    List<BookingDtoReturned> getAllBookingsByOwner(@RequestParam(defaultValue = "ALL") String state,
                                                   @RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(defaultValue = "0") Integer from,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        log.info("Просмотр бронирований владельцем с айди " + userId);
        return bookingService.getAllBookingsByOwner(state, userId, from, size);
    }
}
