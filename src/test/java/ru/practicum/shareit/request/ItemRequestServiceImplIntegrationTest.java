package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoShort;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplIntegrationTest {

    private final ItemRequestServiceImpl itemRequestService;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private ItemRequestDtoShort itemRequestDtoShort;
    private ItemRequestDto itemRequestDto;
    private Long userId;
    private Long requestId;
    private LocalDateTime created;

    @BeforeEach
    void beforeEach() {
        created = LocalDateTime.now();
        User user = new User(null, "Alex", "alex.b@yandex.ru");
        user = userRepository.save(user);
        userId = user.getId();

        ItemRequest itemRequest = new ItemRequest(null, "description", user, created);
        itemRequest = itemRequestRepository.save(itemRequest);
        requestId = itemRequest.getId();

        itemRequestDtoShort = new ItemRequestDtoShort(null, "description", userId, created);
        itemRequestDto = new ItemRequestDto(requestId, "description", userId, created, List.of());
    }

    @Test
    void getById() {
        ItemRequestDto result = itemRequestService.getById(userId, requestId);

        assertEquals(requestId, result.getId());
        assertEquals(userId, result.getRequesterId());
        assertEquals("description", result.getDescription());
    }

    @Test
    void getAll() {
        Integer from = 0;
        Integer size = 10;

        List<ItemRequestDto> result = itemRequestService.getAll(userId, from, size);

        assertEquals(List.of(), result);
    }

    @Test
    void getAllByRequester() {
        Integer from = 0;
        Integer size = 10;

        List<ItemRequestDto> result = itemRequestService.getAllByRequester(userId, from, size);
        List<ItemRequestDto> dtoList = List.of(itemRequestDto);

        assertEquals(dtoList.get(0).getId(), result.get(0).getId());
        assertEquals(dtoList.get(0).getDescription(), result.get(0).getDescription());
        assertEquals(dtoList.get(0).getRequesterId(), result.get(0).getRequesterId());
        assertEquals(dtoList.get(0).getItems().size(), result.get(0).getItems().size());
    }

    @Test
    void create() {
        ItemRequestDto result = itemRequestService.create(requestId, itemRequestDtoShort);

        assertEquals(requestId + 1, result.getId());
        assertEquals(userId, result.getRequesterId());
        assertEquals("description", result.getDescription());
    }
}
