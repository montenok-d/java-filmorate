package ru.yandex.practicum.filmorate.annotation;

import java.lang.annotation.*;

import jakarta.validation.Constraint;


@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReleaseDateValidator.class)
public @interface ReleaseDate {

    String message() default "Введите дату релиза не ранее {value}";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};

    String value();
}