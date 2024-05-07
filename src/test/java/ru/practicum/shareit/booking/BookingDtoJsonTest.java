package ru.practicum.shareit.booking;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        start = LocalDateTime.now().plusDays(1);
        end = start.plusDays(2);

        bookingDto = BookingDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .status(BookingStatus.WAITING)
                .build();

        BookingDto.Booker booker = bookingDto.new Booker(1L, "Alex");
        BookingDto.Item bookingItem = bookingDto.new Item(1L, "Item");
        bookingDto.setBooker(booker);
        bookingDto.setItem(bookingItem);
    }

    @Test
    void testUserDto() throws Exception {
        JsonContent<BookingDto> result = json.write(bookingDto);

        Assertions.assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }

    @Test
    void testSerialize() throws Exception {
        JsonContent<BookingDto> result = json.write(bookingDto);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSS");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(bookingDto.getStart().format(formatter));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(bookingDto.getEnd().format(formatter));
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(bookingDto.getStatus().toString());
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("Alex");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
    }
}