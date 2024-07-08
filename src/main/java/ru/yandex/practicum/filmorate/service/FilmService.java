package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;
    private final MpaService mpaService;
    private final GenreService genreService;
    private final DirectorService directorService;

    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage, UserService userService, MpaService mpaService, GenreService genreService, DirectorService directorService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.mpaService = mpaService;
        this.genreService = genreService;
        this.directorService = directorService;
    }

    public Collection<Film> findAll() {
        log.info("filmStorage.findAll");
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        mpaService.findById(film.getMpa().getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                genreService.findById(genre.getId());
            }
        }
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        findById(film.getId());
        return filmStorage.update(film);
    }

    public void delete(Long id) {
        findById(id);
        filmStorage.delete(id);
    }

    public Film findById(Long id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Film № %d not found", id)));
    }

    public void addLike(Long filmId, Long userId) {
        findById(filmId);
        userService.findById(userId);
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        findById(filmId);
        userService.findById(userId);
        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getPopular(Integer count, Optional<Integer> genreId, Optional<Integer> year) {
        return filmStorage.getPopular(count, genreId, year);
    }

    public List<Film> getDirectorFilms(String sortBy, Long directorId) {
        directorService.findById(directorId);
        if (sortBy.equalsIgnoreCase("year")) {
            return filmStorage.getDirectorFilmsByYear(directorId);
        } else if (sortBy.equalsIgnoreCase("likes")) {
            return filmStorage.getDirectorFilmsByLikes(directorId);
        } else {
            throw new IllegalArgumentException("Unsupported sortBy option: " + sortBy);
        }
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
    }

    public List<Film> searchFilms(String query, String by) {
        String[] searchCriteria = by.toLowerCase().split(",");
        Set<String> criteriaSet = new HashSet<>(Arrays.asList(searchCriteria));

        List<Film> filmsByTitle = new ArrayList<>();
        List<Film> filmsByDirector = new ArrayList<>();

        if (criteriaSet.contains("title")) {
            filmsByTitle = filmStorage.searchByTitle(query);
        }
        if (criteriaSet.contains("director")) {
            filmsByDirector = filmStorage.searchByDirector(query);
        }
        List<Film> combinedFilms = new ArrayList<>();
        for (Film film : filmsByDirector) {
            if (!combinedFilms.contains(film)) {
                combinedFilms.add(film);
            }
        }
        for (Film film : filmsByTitle) {
            if (!combinedFilms.contains(film)) {
                combinedFilms.add(film);
            }
        }
        return combinedFilms;
    }
}