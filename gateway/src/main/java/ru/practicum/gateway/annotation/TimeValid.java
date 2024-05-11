package ru.practicum.gateway.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = DateValidator.class)
@Target(TYPE_USE)
@Retention(RUNTIME)
public @interface TimeValid {
    String message() default "Wrong timecodes.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
