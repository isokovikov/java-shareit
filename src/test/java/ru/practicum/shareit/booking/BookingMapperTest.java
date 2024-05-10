package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookingMapperTest {
    private Item item;
    private User user;
    private Booking booking;
    private final LocalDateTime start = LocalDateTime.now();
    private final LocalDateTime end = LocalDateTime.now().plusDays(2);

    @BeforeEach
    void setUp() {
        user = new User(1L, "Alex", "alex.b@yandex.ru");
        item = new Item(1L, "bag", "description", true, user, null);
        booking = new Booking(1L, start, end, item, user, BookingStatus.WAITING);
    }

    @Test
    void toBookingFromCreateBookingDto() {
        BookingShortDto createBookingDto = new BookingShortDto(user.getId(), start, end, item.getId());
        Booking result = BookingMapper.toBooking(createBookingDto);
        assertThat(result.getStart()).isEqualTo(start);
        assertThat(result.getEnd()).isEqualTo(end);
    }

    @Test
    void toDtoShortFromBooking() {
        BookingShortDto dto = BookingMapper.toBookingShortDto(booking);
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getItemId()).isEqualTo(item.getId());
    }

    @Test
    void toDtoFromBooking() {
        BookingDto dto = BookingMapper.toBookingDto(booking);
        assertThat(dto.getId()).isEqualTo(booking.getId());
        assertThat(dto.getStart()).isEqualTo(booking.getStart());
        assertThat(dto.getEnd()).isEqualTo(booking.getEnd());
        assertThat(dto.getItem()).isEqualToComparingFieldByField(dto.new Item(item.getId(), item.getName()));
        assertThat(dto.getBooker()).isEqualToComparingFieldByField(dto.new Booker(user.getId(), user.getName()));
        assertThat(dto.getStatus()).isEqualTo(booking.getStatus());
    }

    @Test
    void toDtoShortResponseFromBooking() {
        BookingShortDto dto = BookingMapper.toBookingShortDto(booking);
        assertThat(dto.getId()).isEqualTo(booking.getId());
        assertThat(dto.getStart()).isEqualTo(booking.getStart());
        assertThat(dto.getEnd()).isEqualTo(booking.getEnd());
        assertThat(dto.getItemId()).isEqualTo(item.getId());
    }

    @Test
    void toBookingFromItem() {
        BookingForItemDto bookingForItemDto = BookingMapper.toBookingForItemDto(booking);
        assertThat(bookingForItemDto.getId()).isEqualTo(1L);
        assertThat(bookingForItemDto.getStart()).isEqualTo(start);
        assertThat(bookingForItemDto.getEnd()).isEqualTo(end);
        assertThat(bookingForItemDto.getItemId()).isEqualTo(item.getId());
        assertThat(bookingForItemDto.getBookerId()).isEqualTo(user.getId());
    }
}