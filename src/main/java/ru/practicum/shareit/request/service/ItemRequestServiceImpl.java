package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoShort;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    @Override
    public ItemRequestDto create(Long userId, ItemRequestDtoShort itemRequestDtoShort) {
        // Сначала проверяем наличие пользователя
        User requester = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Not found User with Id:" + userId));

        // Теперь, когда мы уверены, что пользователь существует, создаем запрос
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDtoShort);
        itemRequest.setRequester(requester);

        // Сохраняем объект запроса
        itemRequestRepository.save(itemRequest);

        // Возвращаем DTO созданного запроса
        return ItemRequestMapper.toDto(itemRequest);
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Not found User with Id:" + userId);
        }

        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Not Found Request with Id:" + requestId));

        List<ItemDto>  items = new ArrayList<>(ItemMapper.toDtoShortList(itemRepository
                .findByRequest_IdOrderById(requestId)));
        ItemRequestDto itemRequestDto = ItemRequestMapper.toDto(itemRequest);
        itemRequestDto.setItems(items);
        return itemRequestDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getAll(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Not found User with Id:" + userId);
        }
        Pageable pageable = PageRequest.of(from / size, size,
                Sort.by(Sort.Direction.DESC, "created"));

        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdNot(userId, pageable);
        return getItemRequestsDtoWithItems(requests);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getAllByRequester(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Not found User with Id:" + userId);
        }
        Pageable pageable = PageRequest.of(from / size, size,
                Sort.by(Sort.Direction.DESC, "created"));

        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterId(userId, pageable);

        return getItemRequestsDtoWithItems(requests);
    }

    private List<ItemRequestDto> getItemRequestsDtoWithItems(List<ItemRequest> requests) {
        List<Long> requestsId = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        List<Item> items = itemRepository.findByRequest_IdIn(requestsId);

        Map<ItemRequest, List<Item>> itemRequestsByItem = items.stream()
                .collect(groupingBy(Item::getRequest, toList()));

        List<ItemRequestDto> itemRequestDto = new ArrayList<>();

        for (ItemRequest itemRequest : requests) {
            List<Item> itemsTemp = itemRequestsByItem.getOrDefault(itemRequest, List.of());

            List<ItemDto> itemDtoForRequests = itemsTemp.stream()
                    .map(ItemMapper::toItemDto)
                    .collect(toList());
            itemRequestDto.add(ItemRequestMapper.toItemRequestDto(itemRequest, itemDtoForRequests));
        }

        return itemRequestDto;
    }
}