package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoShort;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Mock
    private UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Test
    void create_shouldSaveItemRequest() {
        Long userId = 1L;
        User user = new User(userId, "Alex", "alex.b@yandex.ru");
        ItemRequestDtoShort dtoShort = new ItemRequestDtoShort(1L, "desc", userId, null);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.save(Mockito.any()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ItemRequestDto dto = itemRequestService.create(userId, dtoShort);

        assertThat(dto.getDescription()).isEqualTo(dtoShort.getDescription());
    }

    @Test
    void create_shouldReturnUserNotFoundException() {
        Long userId = 999L;

        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        ItemRequestDtoShort dtoShort = new ItemRequestDtoShort(1L, "desc", userId, null);

        assertThrows(NotFoundException.class, () -> itemRequestService.create(userId, dtoShort));

        Mockito.verify(itemRequestRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void getAllByRequester_shouldReturnItemRequestDtoList() {
        Long userId = 1L;
        User user = new User(userId, "Alex", "alex.b@yandex.ru");
        ItemRequest itemRequest = new ItemRequest(1L, "description", user, null);
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "description", userId,
                null, List.of());

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRequestRepository.findAllByRequesterId(Mockito.anyLong(), Mockito.any()))
                .thenReturn(List.of(itemRequest));

        List<ItemRequestDto> expectedDtoList = List.of(itemRequestDto);
        List<ItemRequestDto> actualDtoList = itemRequestService.getAllByRequester(userId, 0, 10);

        assertEquals(expectedDtoList.get(0).getId(), actualDtoList.get(0).getId());
        assertEquals(expectedDtoList.get(0).getDescription(), actualDtoList.get(0).getDescription());
        assertEquals(expectedDtoList.get(0).getRequesterId(), actualDtoList.get(0).getRequesterId());
        assertEquals(expectedDtoList.get(0).getItems().size(), actualDtoList.get(0).getItems().size());
        assertEquals(expectedDtoList, actualDtoList);
    }

    @Test
    void getAllByRequester_shouldReturnUserNotFoundException() {
        Long userId = 999L;

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemRequestService.getAllByRequester(userId, 0, 10));

        Mockito.verify(itemRequestRepository, Mockito.never()).findAllByUserId(Mockito.anyLong(), Mockito.any());
    }

    @Test
    void getAll_shouldReturnItemRequestDtoList() {
        Long userId = 1L;
        User user = new User(userId, "Alex", "alex.b@yandex.ru");

        ItemRequest itemRequest = new ItemRequest(1L, "description", user, null);
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "description", userId,
                null, List.of());

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRequestRepository.findAllByRequesterIdNot(Mockito.anyLong(), Mockito.any()))
                .thenReturn(List.of(itemRequest));

        List<ItemRequestDto> expectedDtoList = List.of(itemRequestDto);
        List<ItemRequestDto> actualDtoList = itemRequestService.getAll(userId, 0, 10);

        assertEquals(expectedDtoList.get(0).getId(), actualDtoList.get(0).getId());
        assertEquals(expectedDtoList.get(0).getDescription(), actualDtoList.get(0).getDescription());
        assertEquals(expectedDtoList.get(0).getRequesterId(), actualDtoList.get(0).getRequesterId());
        assertEquals(expectedDtoList.get(0).getItems().size(), actualDtoList.get(0).getItems().size());
    }

    @Test
    void getAll_shouldReturnUserNotFoundException() {
        Long userId = 999L;

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemRequestService.getAll(userId, 0, 10));

        Mockito.verify(itemRequestRepository, Mockito.never()).findAllByUserId(Mockito.anyLong(), Mockito.any());
    }

    @Test
    void getById_shouldReturnItemRequestDto() {
        Long userId = 1L;
        Long requestId = 1L;
        User user = new User(userId, "Alex", "alex.b@yandex.ru");
        ItemRequest itemRequest = new ItemRequest(1L, "description",  user,null);

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(itemRequest));
        Mockito.when(itemRepository.findByRequest_IdOrderById(Mockito.anyLong())).thenReturn(List.of());

        ItemRequestDto expected = new ItemRequestDto(1L, "description", userId, null, List.of());
        ItemRequestDto actual = itemRequestService.getById(userId, requestId);

        assertEquals(expected, actual);
    }

    @Test
    void getById_shouldReturnUserNotFoundException() {
        Long userId = 999L;
        Long requestId = 1L;

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemRequestService.getById(userId, requestId));

        Mockito.verify(itemRequestRepository, Mockito.never()).findById(Mockito.anyLong());
    }

    @Test
    void getById_shouldReturnItemRequestNotFoundException() {
        Long userId = 1L;
        Long requestId = 999L;

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getById(userId, requestId));
    }
}