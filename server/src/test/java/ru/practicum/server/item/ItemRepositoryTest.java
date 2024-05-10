package ru.practicum.server.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.request.model.ItemRequest;
import ru.practicum.server.request.repository.ItemRequestRepository;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user;
    private Item item1;
    private Item item2;
    private ItemRequest itemRequest;


    @BeforeEach
    void add() {
        user = new User(null, "Alex", "alex.b@yandex.ru");
        userRepository.save(user);

        item1 = new Item(null, "item bag", "description", true, user,
                null);
        itemRepository.save(item1);
        item2 = new Item(null, "item table", "description", true, user,
                null);
        itemRepository.save(item2);

        itemRequest = new ItemRequest(null, "description", user, LocalDateTime.now());
        itemRequestRepository.save(itemRequest);
    }

    @Test
    void search() {
        Boolean available = true;
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        List<Item> expected = List.of(item1, item2);

        List<Item> actual1 = itemRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAndAvailable("item", "item", available);
        assertEquals(expected, actual1);
        assertEquals(2, actual1.size());

        List<Item> actual2 = itemRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAndAvailable("ITEM", "ITEM", available);
        assertEquals(expected, actual2);
        assertEquals(2, actual2.size());


        List<Item> actual3 = itemRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAndAvailable("desc", "desc", available);
        assertEquals(expected, actual3);
        assertEquals(2, actual3.size());

        List<Item> actual4 = itemRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAndAvailable("ReturnEmpty", "ReturnEmpty", available);
        assertEquals(List.of(), actual4);
    }
}