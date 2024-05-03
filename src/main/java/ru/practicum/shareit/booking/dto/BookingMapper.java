package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        BookingDto bookingDto = BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .build();

        if (booking.getBooker() != null) {
            BookingDto.Booker booker = bookingDto.new Booker(booking.getBooker().getId(), booking.getBooker().getName());
            bookingDto.setBooker(booker);
        }

        if (booking.getItem() != null) {
            BookingDto.Item item = bookingDto.new Item(booking.getItem().getId(), booking.getItem().getName());
            bookingDto.setItem(item);
        }

        return bookingDto;
    }

    public static Booking toBooking(BookingShortDto bookingShortDto) {
        return Booking.builder()
                .id(bookingShortDto.getId())
                .start(bookingShortDto.getStart())
                .end(bookingShortDto.getEnd())
                .build();
    }

    public static BookingShortDto toBookingShortDto(Booking booking) {
        return BookingShortDto.builder()
                .id(booking.getId())
                .itemId(booking.getItem().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }

    public static BookingForItemDto toBookingForItemDto(Booking booking) {
        return BookingForItemDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .itemId(booking.getItem().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }
}
