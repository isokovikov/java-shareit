package ru.practicum.gateway.annotation;

import ru.practicum.gateway.booking.dto.BookItemRequestDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class DateValidator implements ConstraintValidator<TimeValid, BookItemRequestDto> {
    @Override
    public void initialize(TimeValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookItemRequestDto bookingShortDto, ConstraintValidatorContext cxt) {
        LocalDateTime start = bookingShortDto.getStart();
        LocalDateTime end = bookingShortDto.getEnd();
        if (start == null || end == null) {
            return false;
        }
        return start.isBefore(end);
    }
}