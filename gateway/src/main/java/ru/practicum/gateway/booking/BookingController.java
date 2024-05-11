package ru.practicum.gateway.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.gateway.booking.dto.BookItemRequestDto;
import ru.practicum.gateway.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Getting a reservation with the following status {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, bookingStateFrom(stateParam), from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                           @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                             Integer from,
                                           @Positive @RequestParam(name = "size", defaultValue = "10")
                                                             Integer size) {
        log.info("Getting the owner's reservation with the status {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookingCurrentOwner(userId, bookingStateFrom(stateParam), from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating a reservation {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long bookingId) {
        log.info("Getting a reservation {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @PathVariable Long bookingId,
                                                @RequestParam boolean approved) {
        log.info("Confirmation of booking status {}", bookingId);
        return bookingClient.approveStatus(userId, bookingId, approved);
    }

    private static BookingState bookingStateFrom(String stateParam) {
        return BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
    }
}