package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface RequestService {
    ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDto> getUserRequests(Long userId);

    List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size);

    ItemRequestDto getRequestById(Long userId, Long requestId);
}
