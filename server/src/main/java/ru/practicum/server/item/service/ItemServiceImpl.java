package ru.practicum.server.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.booking.dto.BookingForItemDto;
import ru.practicum.server.booking.dto.BookingMapper;
import ru.practicum.server.booking.model.BookingStatus;
import ru.practicum.server.booking.repository.BookingRepository;
import ru.practicum.server.exception.BadRequestException;
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
import ru.practicum.server.request.repository.ItemRequestRepository;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static ru.practicum.server.booking.model.BookingStatus.APPROVED;
import static ru.practicum.server.item.dto.CommentMapper.toComment;
import static ru.practicum.server.item.dto.CommentMapper.toCommentDto;
import static ru.practicum.server.item.dto.ItemMapper.toItem;
import static ru.practicum.server.item.dto.ItemMapper.toItemDto;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private final ItemRequestRepository itemRequestRepository;

    @Override
    public List<ItemDto> getAll(Long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        List<ItemDto> itemDtoList = items.stream()
                .map(ItemMapper::toItemDto)
                .collect(toList());
        //получаем id элементов для загрузки комментов
        List<Long> idItems = itemDtoList.stream()
                .map(ItemDto::getId)
                .sorted()
                .collect(Collectors.toList());
        //загружаем бронирования
        getAllBookingsByItem(itemDtoList, idItems);

        Map<Long, List<CommentDto>> comments = commentRepository.findByItemIdIn(idItems, Sort.by(ASC, "created"))
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.groupingBy(CommentDto::getId));

        itemDtoList.forEach(i -> i.setComments(comments.getOrDefault(i.getId(), Collections.emptyList())));

        //Collections.reverse(itemDtoList);

        return itemDtoList;
    }

    @Override
    public ItemDto getById(Long id, Long ownerId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Not Found Item with Id: " + id));
        List<ItemDto> itemDtoList = new ArrayList<>();
        itemDtoList.add(toItemDto(item));
        if (item.getOwner().getId().equals(ownerId)) {
            List<Long> idItems = itemDtoList.stream().map(ItemDto::getId).collect(Collectors.toList());
            getAllBookingsByItem(itemDtoList, idItems);
        }
        ItemDto itemDto = itemDtoList.get(0);
        itemDto.setComments(commentRepository.findAllByItemId(id)
                .stream().map(CommentMapper::toCommentDto).collect(toList()));

        return itemDto;
    }

    @Transactional
    @Override
    public ItemDto create(ItemShortDto itemShortDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Not possible create Item - " +
                        "Not Found User with Id: " + userId));
        Item item = toItem(itemShortDto);
        item.setOwner(user);
        Long requestId = itemShortDto.getRequestId();

        if (requestId != null) {
            item.setRequest(itemRequestRepository.findById(requestId)
                    .orElseThrow(() -> new NotFoundException("Not found Request with Id:" + requestId)));
        }
        itemRepository.save(item);

        return toItemDto(item);
    }

    @Transactional
    @Override
    public ItemDto update(ItemShortDto itemShortDto, Long id, Long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Not Found Item with Id: " + id));
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Not possible to update the Item from the User with Id: " +
                    userId + "There is no such Item");
        }
        if (itemShortDto.getName() != null && !itemShortDto.getName().isBlank()) {
            item.setName(itemShortDto.getName());
        }
        if (itemShortDto.getDescription() != null && !itemShortDto.getDescription().isBlank()) {
            item.setDescription(itemShortDto.getDescription());
        }
        if (itemShortDto.getAvailable() != null) {
            item.setAvailable(itemShortDto.getAvailable());
        }

        return toItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public void delete(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public List<ItemDto> search(String text) {
        Boolean available = true;
        return itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAndAvailable(text,
                text, available).stream().map(ItemMapper::toItemDto).collect(toList());
    }

    @Transactional
    @Override
    public CommentDto createComment(Long itemId, Long userId, CommentShortDto commentShortDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Not possible create Comment - " +
                        "Does not exist User with Id " + userId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Not possible create Comment - " +
                        "Does not exist Item with Id " + itemId));
        if (bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(userId, itemId, APPROVED,
                now()).isEmpty()) {
            throw new BadRequestException("Not possible create Comment - " +
                    "Item has not been rented by the user or the rental of the item has not yet been completed");
        }
        Comment comment = toComment(commentShortDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(now());
        commentRepository.save(comment);

        return toCommentDto(comment);
    }

    private void getAllBookingsByItem(List<ItemDto> itemDtoList,  List<Long> idItems) {
        Map<Long, BookingForItemDto> lastBookings = bookingRepository.findFirstByItemIdInAndStartLessThanEqualAndStatus(
                        idItems, LocalDateTime.now(), BookingStatus.APPROVED, Sort.by(Sort.Direction.DESC, "start"))
                .stream()
                .map(BookingMapper::toBookingForItemDto)
                .collect(Collectors.toMap(BookingForItemDto::getItemId, Function.identity()));
        itemDtoList.forEach(i -> i.setLastBooking(lastBookings.get(i.getId())));

        Map<Long, BookingForItemDto> nextBookings = bookingRepository.findFirstByItemIdInAndStartAfterAndStatus(
                        idItems, LocalDateTime.now(), BookingStatus.APPROVED, Sort.by(Sort.Direction.ASC, "start"))
                .stream()
                .map(BookingMapper::toBookingForItemDto)
                .collect(Collectors.toMap(BookingForItemDto::getItemId, Function.identity()));
        itemDtoList.forEach(i -> i.setNextBooking(nextBookings.get(i.getId())));

    }
}
