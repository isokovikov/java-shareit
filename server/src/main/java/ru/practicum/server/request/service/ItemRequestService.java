package ru.practicum.server.request.service;

import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.dto.ItemRequestDtoShort;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto getById(Long userId, Long requestId);

    ItemRequestDto create(Long userId, ItemRequestDtoShort itemRequestDtoShort);

    List<ItemRequestDto> getAll(Long userId, Integer from, Integer size);

    List<ItemRequestDto> getAllByRequester(Long userId, Integer from, Integer size);
}