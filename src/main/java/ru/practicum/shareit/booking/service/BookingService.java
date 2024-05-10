package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingShortDto bookingShortDto, Long userId);

    BookingDto approve(Long bookingId, Long userId, Boolean approved);

    List<BookingDto> getAllByOwnerId(Long userId, String state, Integer from, Integer size);


    BookingDto getById(Long itemId, Long userId);

    List<BookingDto> getAllByBookerId(Long userId, String state, Integer from, Integer size);

}