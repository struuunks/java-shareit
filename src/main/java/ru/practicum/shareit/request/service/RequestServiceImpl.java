package ru.practicum.shareit.request.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.InvalidException;
import ru.practicum.shareit.item.dto.ItemDtoOwner;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repo.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestServiceImpl implements RequestService {
    final RequestRepository requestRepository;
    final UserRepository userRepository;
    final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new DataNotFoundException("Пользователь с айди " + userId + " не найден"));
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isEmpty()) {
            throw new InvalidException("Запрос не может быть пустым");
        }
        ItemRequest itemRequest = requestRepository.save(RequestMapper.toItemRequest(itemRequestDto, user));
        return RequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getUserRequests(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new DataNotFoundException("Пользователь с айди " + userId + " не найден"));
        List<Item> items = itemRepository.findAll();
        List<ItemRequestDto> itemRequestDto = requestRepository.findAllByRequestorIdOrderByCreatedAsc(user.getId())
                .stream()
                .map(RequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        return searchItemsForRequests(itemRequestDto, items);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new DataNotFoundException("Пользователь с айди " + userId + " не найден"));
        List<ItemRequestDto> requestDtos = requestRepository.findItemRequestsByRequestorIdNotOrderByCreatedAsc(
                        user.getId(), PageRequest.of(from / size, size))
                .stream()
                .map(RequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        List<Item> items = itemRepository.findAll();
        return searchItemsForRequests(requestDtos, items);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new DataNotFoundException("Пользователь с айди " + userId + " не найден"));
        ItemRequest itemRequest = requestRepository.findById(requestId).orElseThrow(() ->
                new DataNotFoundException("Запрос с айди " + requestId + " не найден"));
        ItemRequestDto itemRequestDto = RequestMapper.toItemRequestDto(itemRequest);
        List<ItemDtoOwner> items = itemRepository.findItemsByRequestId(requestId).stream()
                .map(ItemMapper::toItemDtoOwner)
                .collect(Collectors.toList());
        itemRequestDto.setItems(items);
        return itemRequestDto;
    }

    private List<ItemRequestDto> searchItemsForRequests(List<ItemRequestDto> requestDtos, List<Item> items) {
        for (ItemRequestDto request : requestDtos) {
            List<ItemDtoOwner> answers = new ArrayList<>();
            for (Item item : items) {
                if (item.getRequest() != null) {
                    if (item.getRequest().getId().equals(request.getId())) {
                        answers.add(ItemMapper.toItemDtoOwner(item));
                    }
                }
                request.setItems(answers);
            }
        }
        return requestDtos;
    }
}
