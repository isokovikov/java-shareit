package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, List<Item>> userItemIndex = new LinkedHashMap<>();

    private Long id = 1L;

    @Override
    public List<Item> findAll() {
        log.info("All items was provided.");
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> getAllByUser(Long id) {
        return userItemIndex.getOrDefault(id, List.of());
    }

    @Override
    public Optional<Item> findById(Long id) {
        log.info("Item with ID {} was found.", id);
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Item create(Item item) {
        item.setId(id);
        id++;
        items.put(item.getId(), item);
        log.info("Item with ID {} was created.", item.getId());
        final List<Item> items = userItemIndex.computeIfAbsent(item.getOwner().getId(), k -> new ArrayList<>());
        items.add(item);
        return item;
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        log.info("Item with ID {} was updated.", item.getId());
        return item;
    }

    @Override
    public void delete(Long id) {
        items.remove(id);
        log.info("Item with ID {} was remove.", id);
    }
}