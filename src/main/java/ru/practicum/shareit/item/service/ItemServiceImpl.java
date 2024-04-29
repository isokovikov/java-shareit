package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.ItemMapper.toItem;
import static ru.practicum.shareit.item.dto.ItemMapper.toItemDto;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<ItemDto> getAll(Long userId) {
        return itemRepository.getAllByUser(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No Item with ID: " + id));

        return toItemDto(item);
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Unable to create Item - " +
                        "No User with ID: " + userId));
        Item item = toItem(itemDto);
        item.setOwner(user);
        itemRepository.create(item);

        return toItemDto(item);
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long id, Long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No Item with ID: " + id));
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("User ID: " + userId + " does not own Item ID: " + id);
        }

        updateItemFields(itemDto, item);

        return toItemDto(item);
    }

    @Override
    public void delete(Long id) {
        getById(id);
        itemRepository.delete(id);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) return Collections.emptyList();

        String query = text.toLowerCase();
        return itemRepository.findAll().stream()
                .filter(i -> (i.getName().toLowerCase().contains(query) ||
                        i.getDescription().toLowerCase().contains(query)) &&
                        i.getAvailable())
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void updateItemFields(ItemDto itemDto, Item item) {
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
    }
}
