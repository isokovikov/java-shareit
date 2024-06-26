package ru.practicum.server.item.service;

import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.dto.CommentShortDto;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.dto.ItemShortDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAll(Long userId);

    ItemDto getById(Long id, Long ownerId);

    ItemDto create(ItemShortDto itemShortDto, Long userId);

    ItemDto update(ItemShortDto itemShortDto, Long id, Long userId);

    void delete(Long id);

    List<ItemDto> search(String text);

    CommentDto createComment(Long itemId, Long userId, CommentShortDto commentShortDto);
}