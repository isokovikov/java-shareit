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
        if (bookingShortDto == null) {
            return null;  // Или выбросить исключение, в зависимости от контекста использования
        }

        if (bookingShortDto.getStart() == null || bookingShortDto.getEnd() == null) {
            throw new IllegalArgumentException("Start and end dates must not be null");
        }

        return Booking.builder()
                .id(bookingShortDto.getId())  // Убедитесь, что ID может быть null, если это новое бронирование
                .start(bookingShortDto.getStart())
                .end(bookingShortDto.getEnd())
                .build();
    }

    public static BookingShortDto toBookingShortDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        Long itemId = booking.getItem() != null ? booking.getItem().getId() : null; // Безопасный доступ к ID

        return BookingShortDto.builder()
                .id(booking.getId())
                .itemId(itemId)
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }

    public static BookingForItemDto toBookingForItemDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        Long bookerId = booking.getBooker() != null ? booking.getBooker().getId() : null;
        Long itemId = booking.getItem() != null ? booking.getItem().getId() : null;

        return BookingForItemDto.builder()
                .id(booking.getId())
                .bookerId(bookerId)
                .itemId(itemId)
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }
}
