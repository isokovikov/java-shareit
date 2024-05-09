package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {

    public static ItemRequest toItemRequest(ItemRequestDtoShort shortDto) {
        return ItemRequest.builder()
                .description(Optional.ofNullable(shortDto.getDescription()).orElse(""))
                .created(Optional.ofNullable(shortDto.getCreated()).orElse(LocalDateTime.now()))
                .build();
    }

    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        long requesterId = Optional.ofNullable(itemRequest.getRequester())
                .map(User::getId)
                .orElseThrow(() -> new IllegalStateException("Requester cannot be null"));

        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requesterId(requesterId)
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<ItemDto> items) {
        long requesterId = Optional.ofNullable(itemRequest.getRequester())
                .map(user -> user.getId())
                .orElseThrow(() -> new IllegalStateException("Requester cannot be null"));

        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                requesterId,
                itemRequest.getCreated(),
                Optional.ofNullable(items).orElse(Collections.emptyList())
        );
    }
}