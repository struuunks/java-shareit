package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.InvalidException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOwner;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.validator.ItemValidator;
import ru.practicum.shareit.item.repo.CommentRepository;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repo.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.Status.APPROVED;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {
    final ItemRepository itemRepository;
    final UserRepository userRepository;
    final BookingRepository bookingRepository;
    final CommentRepository commentRepository;
    final RequestRepository requestRepository;
    final ItemValidator validator;

    @Transactional
    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        validator.validate(itemDto, userId);
        User user = userRepository.findById(userId).orElseThrow(() ->
                new DataNotFoundException("Пользователь с айди " + userId + " не найден"));
        Item item = ItemMapper.toItem(itemDto, user, null);
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = requestRepository.findById(itemDto.getRequestId()).orElseThrow(() ->
                    new DataNotFoundException("Запрос с айди " + itemDto.getRequestId() + " не найден"));
            item.setRequest(itemRequest);
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new DataNotFoundException("Вещь с айди " + itemId + " не найдена"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new DataNotFoundException("Пользователь с айди " + userId + "не является владельцем вещи");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDtoOwner getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new DataNotFoundException("Вещь с айди " + itemId + " не найдена"));
        ItemDtoOwner itemDtoOwner = ItemMapper.toItemDtoOwner(item);
        List<CommentDto> comments = commentRepository.findCommentByItemId(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        itemDtoOwner.setComments(comments);
        if (item.getOwner().getId().equals(userId)) {
            Booking last = bookingRepository.findFirstByItemIdAndStartIsBeforeAndStatusIsOrderByStartDesc(
                    itemId, LocalDateTime.now(), APPROVED
            );
            Booking next = bookingRepository.findTopByItemIdAndStartIsAfterAndStatusIsOrderByStartAsc(
                    itemId, LocalDateTime.now(), APPROVED
            );
            if (last != null) {
                itemDtoOwner.setLastBooking(BookingMapper.toBookingDto(last));
            }
            if (next != null) {
                itemDtoOwner.setNextBooking(BookingMapper.toBookingDto(next));
            }
        }
        return itemDtoOwner;
    }

    @Transactional
    @Override
    public CommentDto commentItem(Long itemId, CommentDto commentDto, Long authorId) {
        if (commentDto.getText().isBlank() || commentDto.getText().isEmpty()) {
            throw new InvalidException("Нельзя оставить пустой комментарий о вещи");
        }

        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new DataNotFoundException("Вещь с айди " + itemId + " не найдена"));
        User user = userRepository.findById(authorId).orElseThrow(() ->
                new DataNotFoundException("Пользователь с айди " + authorId + " не найден"));
        Comment comment = CommentMapper.toComment(commentDto, user, item);

        List<Booking> bookings = bookingRepository.findBookingsByBookerIdAndItemId(user.getId(), item.getId())
                .stream()
                .filter(b -> b.getEnd().isBefore(LocalDateTime.now()) && b.getStatus().equals(APPROVED))
                .collect(Collectors.toList());

        if (bookings.isEmpty()) {
            throw new InvalidException(
                    "Пользователь с айди " + user.getId() + " не бронировал вещь с айди " + item.getId()
            );
        }
        List<Comment> comments = commentRepository.findCommentByItemId(itemId)
                .stream()
                .filter(c -> c.getAuthor().getId().equals(user.getId()))
                .collect(Collectors.toList());
        if (!comments.isEmpty()) {
            throw new InvalidException("Пользователь уже коментировал эту вещь");
        }
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDtoOwner> viewAllItems(Long userId, Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            throw new InvalidException("Индекс первого элемента и количество элементов для отображения" +
                    " не могут быть меньше нуля");
        }
        LocalDateTime ldt = LocalDateTime.now();
        List<Booking> lastBookings = bookingRepository.findBookingsByStatusAndEndIsBeforeOrderByStartDesc(APPROVED, ldt);
        List<Booking> nextBookings = bookingRepository.findBookingsByStatusAndStartIsAfterOrderByStartAsc(APPROVED, ldt);
        List<ItemDtoOwner> items = new ArrayList<>();
        for (Item i : itemRepository.findByOwnerIdOrderByIdAsc(userId, PageRequest.of(from / size, size))) {
            List<Booking> lastBookingsByItem = lastBookings.stream()
                    .filter(b -> b.getItem().getId().equals(i.getId()))
                    .collect(Collectors.toList());
            List<Booking> nextBookingsByItem = nextBookings.stream()
                    .filter(b -> b.getItem().getId().equals(i.getId()))
                    .collect(Collectors.toList());
            ItemDtoOwner item = ItemMapper.toItemDtoOwner(i);
            if (!lastBookingsByItem.isEmpty()) {
                item.setLastBooking(BookingMapper.toBookingDto(lastBookingsByItem.get(0)));
            }
            if (!nextBookingsByItem.isEmpty()) {
                item.setNextBooking(BookingMapper.toBookingDto(nextBookingsByItem.get(0)));
            }
            items.add(item);
        }
        return items;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> searchItems(String text, Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            throw new InvalidException("Индекс первого элемента и количество элементов для отображения" +
                    " не могут быть меньше нуля");
        }
        if (text.isBlank()) {
            return Collections.emptyList();
        } else {
            return itemRepository.search(text, PageRequest.of(from / size, size))
                    .stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }
}
