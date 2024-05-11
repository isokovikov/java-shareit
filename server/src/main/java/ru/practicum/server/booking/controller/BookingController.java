package ru.practicum.server.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.dto.BookingShortDto;
import ru.practicum.server.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestBody BookingShortDto bookingShortDto,
                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("POST:/bookings request received with parameters {}", bookingShortDto);
        return bookingService.create(bookingShortDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestParam Boolean approved) {
        log.info("PATCH:/{bookingsId} request received with parameters: bookingId = {}, userId = {}, approved = {}",
                bookingId, userId, approved);
        return bookingService.approve(bookingId, userId, approved);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                          @RequestParam(defaultValue = "ALL") String state,
                                          @RequestParam(defaultValue = "0") Integer from,
                                          @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /bookings/owner?state={}&from={}&size={}.", state, from, size);
        return bookingService.getAllByOwnerId(ownerId, state, from, size);
    }

    @GetMapping
    public List<BookingDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                         @RequestParam(defaultValue = "ALL") String state,
                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /bookings?state={}&from={}&size={}.", state, from, size);
        return bookingService.getAllByBookerId(bookerId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET:/{bookingsId} request received with parameters: userId = {}", userId);
        return bookingService.getById(bookingId, userId);
    }
}