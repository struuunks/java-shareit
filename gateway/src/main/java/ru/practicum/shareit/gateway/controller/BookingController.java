package ru.practicum.shareit.gateway.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.client.BookingClient;
import ru.practicum.shareit.gateway.dto.BookingDto;
import ru.practicum.shareit.gateway.dto.enums.State;
import ru.practicum.shareit.gateway.exception.UnsupportedStateException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingController {

    final BookingClient client;

    @Valid
    @PostMapping()
    ResponseEntity<Object> createBooking(@RequestBody BookingDto bookingDto,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Создание нового бронирования пользователем с айди " + userId);
        BookingDto.validate(bookingDto);

        return client.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    ResponseEntity<Object> bookingConfirmation(@PathVariable Long bookingId, @RequestParam Boolean approved,
                                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Подтверждение бронирования с айди " + bookingId + " владельцем вещи");

        return client.bookingConfirmation(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    ResponseEntity<Object> getBookingById(@PathVariable Long bookingId,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Просмотр бронирования с айди " + bookingId);

        return client.getBookingById(userId, bookingId);
    }

    @GetMapping()
    ResponseEntity<Object> getAllBookingsByUser(@RequestParam(name = "state", defaultValue = "ALL") String stateString,
                                                @RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Просмотр бронирований со статусом " + stateString + " пользователем с айди " + userId +
                " , from={}, size={}", from, size);
        State state = State.from(stateString)
                .orElseThrow(() -> new UnsupportedStateException("Unknown state: " + stateString));

        return client.getAllBookingsByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    ResponseEntity<Object> getAllBookingsByOwner(@RequestParam(name = "state", defaultValue = "ALL") String stateString,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                 @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Просмотр бронирований со статусом " + stateString + " владельцем с айди " + userId +
                " , from={}, size={}", from, size);
        State state = State.from(stateString)
                .orElseThrow(() -> new UnsupportedStateException("Unknown state: " + stateString));

        return client.getAllBookingsByOwner(userId, state, from, size);
    }
}
