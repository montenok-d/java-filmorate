package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;
    private final DirectorService directorService;

    @GetMapping
    public Collection<Film> findAll() {
        log.info("GET / films");
        return filmService.findAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("POST / film / {}", film.getName());
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("PUT / film / {}", film.getName());
        return filmService.update(film);
    }

    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable("id") int id) {
        log.info("GET /film/{}", id);
        return filmService.findFilmById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable("id") int id) {
        log.info("DELETE /film/{}", id);
        filmService.delete(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") int id,
                        @PathVariable("userId") int userId) {
        log.info("PUT /{}/like/{}", id, userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") int id,
                           @PathVariable("userId") int userId) {
        log.info("DELETE /{}/like/{}", id, userId);
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") @Positive int count, @RequestParam Optional<Integer> genreId, @RequestParam Optional<Integer> year) {
        log.info("GET /popular count={}", count);
        return filmService.getPopular(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public ResponseEntity<List<Film>> getDirectorFilms(@RequestParam String sortBy, @PathVariable("directorId") Long directorId) {
        try {
            if (!directorService.isDirectorExist(directorId)) {
                return ResponseEntity.notFound().build();
            }
            List<Film> films = filmService.getDirectorFilms(sortBy, directorId);
            return ResponseEntity.ok(films);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(
            @RequestParam(name = "userId") int id,
            @RequestParam(name = "friendId") int friendId) {
        log.info("GET /common userId={} friendId={}", id, friendId);
        return filmService.getCommonFilms(id, friendId);
    }
}
