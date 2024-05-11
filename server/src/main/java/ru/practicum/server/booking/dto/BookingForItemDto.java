package ru.practicum.server.booking.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class BookingForItemDto {
    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private Long itemId;

    private Long bookerId;
}
