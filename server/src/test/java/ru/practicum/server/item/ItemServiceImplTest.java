package ru.practicum.server.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.server.booking.dto.BookingMapper;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.model.BookingStatus;
import ru.practicum.server.booking.repository.BookingRepository;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.dto.CommentMapper;
import ru.practicum.server.item.dto.CommentShortDto;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.dto.ItemMapper;
import ru.practicum.server.item.dto.ItemShortDto;
import ru.practicum.server.item.model.Comment;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.repository.CommentRepository;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.item.service.ItemServiceImpl;
import ru.practicum.server.request.model.ItemRequest;
import ru.practicum.server.request.repository.ItemRequestRepository;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    private User user;
    private ItemDto itemDto;
    private CommentShortDto commentShortDto;
    private Item item;
    private Comment comment;
    private Booking booking;
    private ItemRequest itemRequest;
    private ItemDto itemDtoResponse;

    private ItemShortDto createItemShortDto;

    private ItemShortDto updateItemShortDto;

    @BeforeEach
    void create() {
        user = new User(1L, "Alex", "alex.b@yandex.ru");

        item = new Item(1L, "bag", "description", true, user,
                null);

        createItemShortDto = new ItemShortDto(1L, "name", "description", true, 1L);

        updateItemShortDto = new ItemShortDto(1L, "new name", "new description", false, 1L);

        itemDto = new ItemDto(1L, "name", "description", true, null,
                null, null, 1L);

        itemDtoResponse = new ItemDto(1L, "name", "description", true, null,
                null, null, null);

        itemRequest = new ItemRequest(1L, "description", new User(), null);

        booking = new Booking(1L, null, null, item, user, BookingStatus.WAITING);

        comment = new Comment(1L, "comment", item, user, null);

        commentShortDto = new CommentShortDto(1L, "comment", "Alex", null);
    }


    @Test
    void getByUserId_shouldReturnItemDtoList() {

        Long userId = 1L;
        List<Item> page = List.of(item);
        Sort sort = Sort.by(Sort.Direction.ASC, "id");

        Mockito.when(itemRepository.findAllByOwnerId(user.getId(), sort)).thenReturn(page);

        Mockito.when(bookingRepository.findFirstByItemIdInAndStartLessThanEqualAndStatus(Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Optional.of(booking));

        Mockito.when(bookingRepository.findFirstByItemIdInAndStartAfterAndStatus(Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Optional.of(booking));

        Mockito.when(commentRepository.findByItemIdIn(Mockito.any(), Mockito.any())).thenReturn(List.of(comment));

        itemDtoResponse = ItemMapper.toItemDto(item);
        itemDtoResponse.setComments(List.of(CommentMapper.toCommentDto(comment)));
        itemDtoResponse.setLastBooking(BookingMapper.toBookingForItemDto(booking));
        itemDtoResponse.setNextBooking(BookingMapper.toBookingForItemDto(booking));

        List<ItemDto> expectedDtoList = List.of(itemDtoResponse);
        List<ItemDto> actualDtoList = itemService.getAll(userId);

        Assertions.assertEquals(expectedDtoList, actualDtoList);
    }

    @Test
    void getById_shouldReturnItemDto() {
        Long userId = 1L;
        Long itemId = 1L;

        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.findAllByItemId(Mockito.anyLong())).thenReturn(Collections.emptyList());

        assertThat(itemService.getById(itemId, userId)).isEqualTo(ItemMapper.toItemDto(item));
    }

    @Test
    void getById_shouldReturnItemNotFoundException() {
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Long itemId = 999L;
        Long userId = 1L;

        assertThrows(NotFoundException.class, () -> itemService.getById(itemId, userId));
    }

    @Test
    void search() {
        Mockito.when(itemRepository
                        .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAndAvailable("text",
                                "text", true))
                .thenReturn(List.of(item));

        List<ItemDto> expectedDtoList = List.of(ItemMapper.toItemDto(item));
        List<ItemDto> actualDtoList = itemService.search("text");

        assertEquals(expectedDtoList, actualDtoList);
    }

    @Test
    void getByIdWithUserIsNotOwner_shouldFoundItemDtoWithoutBooking() {
        Long userId = 1L;
        Long itemId = 1L;

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.findAllByItemId(Mockito.any())).thenReturn(List.of(comment));

        itemDto = ItemMapper.toItemDto(item);
        itemDto.setComments(List.of(CommentMapper.toCommentDto(comment)));


        ItemDto expectedDto = itemDto;
        ItemDto actualDto = itemService.getById(itemId, userId);

        assertEquals(expectedDto, actualDto);
    }

    @Test
    void create_shouldSaveItemWithItemRequest() {
        Long userId = 1L;

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemRequest));

        ItemDto expectedDto = new ItemDto(1L, "name", "description", true,
                null, null, List.of(), 1L);
        ItemDto actualDto = itemService.create(createItemShortDto, userId);

        assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
    }

    @Test
    void createWithOwnerNotFound_shouldReturnUserNotFoundException() {
        Long userId = 999L;

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.create(createItemShortDto, userId));

        Mockito.verify(itemRequestRepository, Mockito.never()).findById(Mockito.any());
        Mockito.verify(itemRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void update_shouldUpdateItemName() {
        Long itemId = 1L;
        Long userId = 1L;

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(item);

        System.out.println(userRepository.findById(userId));

        System.out.println(itemRepository.findById(itemId));

        ItemDto itemDto = itemService.update(updateItemShortDto, itemId, userId);

        assertThat(itemDto.getName()).isEqualTo("new name");
    }

    @Test
    void update_shouldUpdateItemDescription() {
        Long itemId = 1L;
        Long userId = 1L;

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(item);

        ItemDto itemDto = itemService.update(updateItemShortDto, itemId, userId);

        assertThat(itemDto.getDescription()).isEqualTo("new description");
    }

    @Test
    void update_shouldUpdateItemAvailable() {
        Long itemId = 1L;
        Long userId = 1L;

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(item);

        ItemDto itemDto = itemService.update(updateItemShortDto, itemId, userId);

        assertThat(itemDto.getAvailable()).isFalse();
    }

    @Test
    void createComment_shouldSaveComment() {
        Long itemId = 1L;
        Long userId = 1L;

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.save(Mockito.any())).thenReturn(comment);
        List<Booking> bookingList = List.of(booking);
        Mockito.when(bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(),
                Mockito.any())).thenReturn(bookingList);

        CommentDto actualDdo = itemService.createComment(itemId, userId, commentShortDto);

        assertEquals(1L, actualDdo.getId());
        assertEquals("comment", actualDdo.getText());
        assertEquals("Alex", actualDdo.getAuthorName());
    }

    @Test
    void delete_shouldDeleteItemAndReturnDeletedItem() {
        Long itemId = 1L;
        itemService.delete(itemId);
        Mockito.verify(itemRepository).deleteById(itemId);
    }
}