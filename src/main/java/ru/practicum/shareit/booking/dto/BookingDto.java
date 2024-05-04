package ru.practicum.shareit.booking.dto;

import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.BookingStatus;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class BookingDto {
    private Long id;

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;

    @NotNull
    private BookingStatus status;

    @Valid
    private Booker booker;

    @Valid
    private Item item;

    @Data
    public class Booker {
        private final long id;
        private final String name;
    }

    @Data
    public class Item {
        private final long id;
        private final String name;
    }
}
