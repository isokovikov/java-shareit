package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoShort;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto getById(Long userId, Long requestId);

    ItemRequestDto create(Long userId, ItemRequestDtoShort itemRequestDtoShort);

    List<ItemRequestDto> getAll(Long userId, Integer from, Integer size);

    List<ItemRequestDto> getAllByRequester(Long userId, Integer from, Integer size);
}