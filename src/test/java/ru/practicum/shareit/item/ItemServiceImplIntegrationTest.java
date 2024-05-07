package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIntegrationTest {
    private final ItemServiceImpl itemService;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private Long itemId;
    private Long userId;
    private Long commentId;
    private User user;
    private Item item;
    private Comment comment;
    private Booking booking;

    @BeforeEach
    void beforeEach() {
        user = new User(null, "Alex", "alex.b@yandex.ru");
        user = userRepository.save(user);
        userId = user.getId();

        item = new Item(null, "item bag", "description", true, user,
                null);
        item = itemRepository.save(item);
        itemId = item.getId();

        booking = new Booking(null, LocalDateTime.now(), LocalDateTime.now(), item, user, BookingStatus.APPROVED);
        booking = bookingRepository.save(booking);

        comment = new Comment(null, "comment", item, user, LocalDateTime.now());
        comment = commentRepository.save(comment);
        commentId = comment.getId();
    }

    @Test
    void getAll() {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setComments(List.of(CommentMapper.toCommentDto(comment)));
        itemDto.setLastBooking(BookingMapper.toBookingForItemDto(booking));

        List<ItemDto> actualDtoList = itemService.getAll(userId);
        List<ItemDto> expectedDtoList = List.of(itemDto);
        System.out.println(expectedDtoList.size());
        System.out.println(actualDtoList.size());

        Assertions.assertEquals(expectedDtoList.get(0).getId(), actualDtoList.get(0).getId());
    }

    @Test
    void search() {
        String text = "desc";

        ItemDto itemDto = ItemMapper.toItemDto(item);

        List<ItemDto> actualDtoList = itemService.search(text);
        List<ItemDto> expectedDtoList = List.of(itemDto);

        Assertions.assertEquals(expectedDtoList.get(0).getDescription(), actualDtoList.get(0).getDescription());
    }

    @Test
    void getById() {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setComments(List.of(CommentMapper.toCommentDto(comment)));
        itemDto.setLastBooking(BookingMapper.toBookingForItemDto(booking));

        ItemDto actualDto = itemService.getById(itemId, userId);

        Assertions.assertEquals(itemDto.getId(), actualDto.getId());
    }

    @Test
    void create() {
        ItemShortDto createItemDto = new ItemShortDto(null, "new", "description",
                true, null);

        ItemDto saveItem = itemService.create(createItemDto, userId);

        Assertions.assertEquals(itemId + 1, saveItem.getId());
        Assertions.assertEquals("new", saveItem.getName());
        Assertions.assertEquals(true, saveItem.getAvailable());
    }

    @Test
    void delete() {
        ItemShortDto createItemDto = new ItemShortDto(null, "new", "description",
                true, null);
        ItemDto saveItem = itemService.create(createItemDto, userId);
        Long id = saveItem.getId();

        itemService.delete(id);

        assertThatThrownBy(() -> itemService.getById(id, userId)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void createComment() {
        comment = new Comment(null, "comment", item, user, LocalDateTime.now());
        comment = commentRepository.save(comment);

        Assertions.assertEquals(commentId + 1, comment.getId());
        Assertions.assertEquals("comment", comment.getText());
        Assertions.assertEquals(item, comment.getItem());
        Assertions.assertEquals(user, comment.getAuthor());
    }
}