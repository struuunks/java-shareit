package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoReceived;
import ru.practicum.shareit.booking.dto.BookingDtoReturned;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.InvalidException;
import ru.practicum.shareit.exception.UnsupportedStateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingServiceImpl implements BookingService {
    final BookingRepository bookingRepository;
    final ItemRepository itemRepository;
    final UserRepository userRepository;

    @Transactional
    @Override
    public BookingDtoReturned createBooking(BookingDtoReceived bookingDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new DataNotFoundException("Пользователь с айди " + userId + " не найден"));
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() ->
                new DataNotFoundException("Вещи с айди " + bookingDto.getItemId() + " не найдена"));
        if (item.getOwner().getId().equals(userId)) {
            throw new DataNotFoundException("Владелец вещи не может создать ее бронирование");
        }
        if (!item.getAvailable()) {
            throw new InvalidException("Вещь с айди " + item.getId() + " недоступна для бронирования");
        }
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new InvalidException("Не указаны дата и время начала или окончания брони");
        }
        if (bookingDto.getStart().isBefore(LocalDateTime.now()) || bookingDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new InvalidException("Бронь не может начаться или закончиться раньше текущей даты и времени");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getEnd().isEqual(bookingDto.getStart())) {
            throw new InvalidException("Бронь не может закончиться одновременно или раньше даты и времени начала брони");
        }

        Booking booking = BookingMapper.toBooking(bookingDto, user, item);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.WAITING);

        return BookingMapper.toBookingDtoReturned(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingDtoReturned bookingConfirmation(Long bookingId, Boolean confirmation, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new DataNotFoundException("Бронирование с айди " + bookingId + " не найдено"));
        User user = userRepository.findById(userId).orElseThrow(() ->
                new DataNotFoundException("Пользователь с айди " + userId + " не найден"));
        if (!booking.getItem().getOwner().getId().equals(user.getId())) {
            throw new DataNotFoundException("Пользователь с айди " + userId +
                    " не является владельцем вещи и не может изменять статус бронирования");
        }
        if (booking.getStatus().equals(Status.WAITING) && confirmation) {
            booking.setStatus(Status.APPROVED);
        } else if (booking.getStatus().equals(Status.APPROVED)) {
            throw new InvalidException("Вещь уже доступна для бронирования");
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingDtoReturned(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDtoReturned getBookingById(Long bookingId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new DataNotFoundException("Пользователь с айди " + userId + " не найден"));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new DataNotFoundException("Бронирование с айди " + bookingId + " не найдено"));
        if (!user.getId().equals(booking.getItem().getOwner().getId()) &&
                !user.getId().equals(booking.getBooker().getId())) {
            throw new DataNotFoundException("Бронирование недоступно, пользователь с айди "
                    + userId + " не является владельцем или арендатором вещи");
        }
        return BookingMapper.toBookingDtoReturned(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDtoReturned> getAllBookingsByUser(String state, Long userId, Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            throw new InvalidException("Индекс первого элемента и количество элементов для отображения" +
                    " не могут быть меньше нуля");
        }
        User user = userRepository.findById(userId).orElseThrow(() ->
                new DataNotFoundException("Пользователь с айди " + userId + " не найден"));
        List<Booking> bookings = bookingRepository.findBookingsByBookerIdOrderByIdDesc(user.getId(),
                PageRequest.of(from / size, size));
        List<Booking> bookingsByState;
        try {
            bookingsByState = bookingsByState(State.valueOf(state.toUpperCase()), bookings);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingsByState.stream()
                .map(BookingMapper::toBookingDtoReturned)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDtoReturned> getAllBookingsByOwner(String state, Long ownerId, Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            throw new InvalidException("Индекс первого элемента и количество элементов для отображения" +
                    " не могут быть меньше нуля");
        }
        User user = userRepository.findById(ownerId).orElseThrow(() ->
                new DataNotFoundException("Пользователь с айди " + ownerId + " не найден"));
        List<Booking> bookings = bookingRepository.findBookingsByItemOwnerIdOrderByIdDesc(user.getId(),
                PageRequest.of(from / size, size));
        List<Booking> bookingsByState;
        try {
            bookingsByState = bookingsByState(State.valueOf(state.toUpperCase()), bookings);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingsByState.stream()
                .map(BookingMapper::toBookingDtoReturned)
                .collect(Collectors.toList());
    }

    private List<Booking> bookingsByState(State state, List<Booking> bookingsDB) {
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings = bookingsDB.stream()
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                bookings = bookingsDB.stream()
                        .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                bookings = bookingsDB.stream()
                        .filter(b -> b.getStart().isBefore(LocalDateTime.now()) &&
                                b.getEnd().isAfter(LocalDateTime.now()))
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .collect(Collectors.toList());
                break;
            case PAST:
                bookings = bookingsDB.stream()
                        .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .collect(Collectors.toList());
                break;
            case WAITING:
                bookings = bookingsDB.stream()
                        .filter(b -> b.getStatus().equals(Status.WAITING))
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .collect(Collectors.toList());
                break;
            case REJECTED:
                bookings = bookingsDB.stream()
                        .filter(b -> b.getStatus().equals(Status.REJECTED))
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .collect(Collectors.toList());
                break;
        }
        return bookings;
    }
}
