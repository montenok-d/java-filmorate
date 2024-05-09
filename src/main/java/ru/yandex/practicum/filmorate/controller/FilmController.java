package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        if (validateDate(film)) {
            film.setId(getNextId());
            films.put(film.getId(), film);
            log.info("Saved film: {}", film);
        } else {
            throw new ValidationException("Can't create the film");
        }
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        if (films.containsKey(newFilm.getId())) {
            if (validateDate(newFilm)) {
                films.put(newFilm.getId(), newFilm);
                log.info("Updated film: {}", newFilm);
            }
        } else {
            throw new ValidationException("Can't update the film");
        }
        return newFilm;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private boolean validateDate(Film film) {
        LocalDate releaseDate = film.getReleaseDate();
        return releaseDate.isAfter(LocalDate.of(1895, 12, 28));
    }
}
