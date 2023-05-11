package ru.practicum.shareit.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.dto.BookingDtoReceived;
import ru.practicum.shareit.booking.dto.BookingDtoReturned;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingController {
    final BookingServiceImpl bookingService;

    @PostMapping
    public BookingDtoReturned createBooking(@RequestBody BookingDtoReceived bookingDto,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Создание нового бронирования");
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoReturned bookingConfirmation(@PathVariable Long bookingId, @RequestParam Boolean approved,
                                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Подтверждение бронирования с айди " + bookingId + " владельцем вещи");
        return bookingService.bookingConfirmation(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoReturned getBookingById(@PathVariable Long bookingId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Просмотр бронирования с айди " + bookingId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoReturned> getAllBookingsByUser(@RequestParam(defaultValue = "ALL") String state,
                                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Просмотр бронирований пользователем с айди " + userId);
        return bookingService.getAllBookingsByUser(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDtoReturned> getAllBookingsByOwner(@RequestParam(defaultValue = "ALL") String state,
                                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Просмотр бронирований владельцем с айди " + userId);
        return bookingService.getAllBookingsByOwner(state, userId);
    }
}
