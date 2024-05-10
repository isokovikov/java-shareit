package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ItemAndCommentMapperTest {
    private Item item;
    private ItemDto itemDto;
    private ItemShortDto itemShortDto;
    private User user;
    private Booking booking;
    private Comment comment;
    private final LocalDateTime start = LocalDateTime.now();
    private final LocalDateTime end = LocalDateTime.now().plusDays(2);

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "Alex", "alex.b@yandex.ru");
        item = new Item(1L, "bag", "description", true, user,
                null);
        comment = new Comment(1L, "text", item, user, start);
        booking = new Booking(1L, start, end, item, user, BookingStatus.WAITING);
        itemDto = ItemMapper.toItemDto(item);
        itemShortDto = new ItemShortDto(1L, "Alex", "description", true, 1L);
    }

    @Test
    void toItemDto() {
        ItemDto itemDto = ItemMapper.toItemDto(item);

        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("bag");
        assertThat(itemDto.getDescription()).isEqualTo("description");
        assertThat(itemDto.getAvailable()).isEqualTo(true);
        assertThat(itemDto.getComments()).isEqualTo(List.of());
        assertThat(itemDto.getRequestId()).isEqualTo(null);

    }

    @Test
    void toItem() {
        Item item = ItemMapper.toItem(itemDto);

        assertThat(item.getId()).isEqualTo(1L);
        assertThat(item.getName()).isEqualTo("bag");
        assertThat(item.getDescription()).isEqualTo("description");
        assertThat(item.getAvailable()).isEqualTo(true);
        assertThat(item.getOwner()).isEqualTo(null);
        assertThat(item.getRequest()).isEqualTo(null);
    }

    @Test
    void itemShortDtoToItem() {
        Item item = ItemMapper.toItem(itemShortDto);

        assertThat(item.getId()).isEqualTo(1L);
        assertThat(item.getName()).isEqualTo("Alex");
        assertThat(item.getDescription()).isEqualTo("description");
        assertThat(item.getAvailable()).isEqualTo(true);
        assertThat(item.getOwner()).isEqualTo(null);
        assertThat(item.getRequest()).isEqualTo(null);
    }

    @Test
    void toDtoShortList() {
        List<Item> itemList = List.of(item);
        List<ItemDto> itemDtoList = ItemMapper.toDtoShortList(itemList);

        assertThat(itemList.get(0).getId()).isEqualTo(itemDtoList.get(0).getId());
        assertThat(itemList.get(0).getName()).isEqualTo(itemDtoList.get(0).getName());
        assertThat(itemList.get(0).getDescription()).isEqualTo(itemDtoList.get(0).getDescription());
        assertThat(itemList.get(0).getAvailable()).isEqualTo(itemDtoList.get(0).getAvailable());
        assertThat(itemList.get(0).getRequest()).isEqualTo(null);
    }

    @Test
    void toCommentDto() {
        CommentDto commentDto = CommentMapper.toCommentDto(comment);

        assertThat(commentDto.getId()).isEqualTo(comment.getId());
        assertThat(commentDto.getText()).isEqualTo(comment.getText());
        assertThat(commentDto.getAuthorName()).isEqualTo(comment.getAuthor().getName());
        assertThat(commentDto.getCreated()).isEqualTo(comment.getCreated());
    }

    @Test
    void toComment() {
        CommentDto commentDto = new CommentDto(1L, "text", "Alex", start);
        Comment comment = CommentMapper.toComment(commentDto);

        assertThat(commentDto.getId()).isEqualTo(comment.getId());
        assertThat(commentDto.getText()).isEqualTo(comment.getText());
        assertThat(commentDto.getCreated()).isEqualTo(comment.getCreated());
    }

    @Test
    void commentShortDtoToComment() {
        CommentShortDto commentShortDto = new CommentShortDto(1L, "text", "Alex", start);
        Comment comment = CommentMapper.toComment(commentShortDto);

        assertThat(commentShortDto.getId()).isEqualTo(comment.getId());
        assertThat(commentShortDto.getText()).isEqualTo(comment.getText());
        assertThat(commentShortDto.getCreated()).isEqualTo(comment.getCreated());
    }
}