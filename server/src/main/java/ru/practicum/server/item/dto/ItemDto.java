package ru.practicum.server.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.server.booking.dto.BookingForItemDto;

import java.util.List;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@EqualsAndHashCode
public class ItemDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingForItemDto lastBooking;

    private BookingForItemDto nextBooking;

    private List<CommentDto> comments;

    private Long requestId;
}
