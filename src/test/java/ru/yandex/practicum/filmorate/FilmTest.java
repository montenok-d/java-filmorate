package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmTest {

    private Film film;
    private Validator validator;
    private ValidatorFactory validatorFactory;

    @BeforeEach
    void setUp() {
        film = Film.builder()
                .name("Film")
                .description("Description")
                .releaseDate(LocalDate.of(1999, 12, 1))
                .duration(100)
                .build();
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    void shouldCreateFilm() {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldNotCreateFilmWithoutName() {
        Film film1 = Film.builder()
                .name(" ")
                .description("Description")
                .releaseDate(LocalDate.parse("1995-12-27"))
                .duration(100)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film1);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void shouldNotCreateFilmWithWrongDate() {
        Film film1 = Film.builder()
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.parse("1795-12-27"))
                .duration(100)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film1);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void shouldNotCreateFilmIfDurationIsNegative() {
        Film film1 = Film.builder()
                .name("Film")
                .description("Description")
                .releaseDate(LocalDate.parse("1995-12-27"))
                .duration(-100)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film1);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }
}
