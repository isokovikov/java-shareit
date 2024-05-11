package ru.practicum.gateway.booking.dto;

import java.util.Optional;

/**
 * Состояния бронирования.
 */
public enum BookingState {
    /** Все бронирования. */
    ALL,

    /** Текущие бронирования. */
    CURRENT,

    /** Будущие бронирования. */
    FUTURE,

    /** Завершенные бронирования. */
    PAST,

    /** Отклоненные бронирования. */
    REJECTED,

    /** Бронирования, ожидающие подтверждения. */
    WAITING;

    /**
     * Конвертирует строку в соответствующее состояние бронирования.
     *
     * @param stringState строковое представление состояния
     * @return {@link Optional} объект {@link BookingState}, если существует, иначе пустой
     */
    public static Optional<BookingState> from(String stringState) {
        for (BookingState state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}