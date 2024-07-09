package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
        log.info("GET /films");
        return filmService.findAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("POST /film/{}", film.getName());
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("PUT /film/{}", film.getName());
        return filmService.update(film);
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable("id") Long id) {
        log.info("GET /film/{}", id);
        return filmService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable("id") Long id) {
        log.info("DELETE /film/{}", id);
        filmService.delete(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Long filmId,
                        @PathVariable("userId") Long userId) {
        log.info("PUT /{}/like/{}", filmId, userId);
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Long filmId,
                           @PathVariable("userId") Long userId) {
        log.info("DELETE /{}/like/{}", filmId, userId);
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") @Positive Integer count,
                                 @RequestParam Optional<Integer> genreId,
                                 @RequestParam Optional<Integer> year) {
        log.info("GET /popular count={}", count);
        return filmService.getPopular(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getDirectorFilms(@RequestParam String sortBy, @PathVariable("directorId") Long directorId) {
        log.info("GET /directorFilms/{}", directorId);
        return filmService.getDirectorFilms(sortBy, directorId);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam(name = "userId") Long userId,
                                     @RequestParam(name = "friendId") Long friendId) {
        log.info("GET /common userId={} friendId={}", userId, friendId);
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/search")
    public List<Film> searchFilms(@RequestParam String query,
                                  @RequestParam String by) {
        log.info("GET /films/search?query={}&by={}", query, by);
        return filmService.searchFilms(query, by);
    }
}