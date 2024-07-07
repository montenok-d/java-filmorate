package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

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
        mpaService.checkMpaForFilm(film.getMpa().getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                genreService.checkGenreById(genre.getId());
            }
        }

        return filmStorage.create(film);
    }

    public Film update(Film film) {
        findFilmById(film.getId());
        return filmStorage.update(film);
    }

    public void delete(long id) {
        findFilmById(id);
        filmStorage.delete(id);
    }

    public Film findFilmById(long id) {
        return filmStorage.findFilmById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Film № %d not found", id)));
    }

    public void addLike(long id, long userId) {
        findFilmById(id);
        userService.findUserById(userId);
        filmStorage.addLike(id, userId);
    }

    public void deleteLike(long id, long userId) {
        findFilmById(id);
        userService.findUserById(userId);
        filmStorage.deleteLike(id, userId);
    }

    public List<Film> getPopular(int count, Optional<Integer> genreId, Optional<Integer> year) {
        return filmStorage.getPopular(count, genreId, year);
    }

    public List<Film> getDirectorFilms(String sortBy, Long id) {
        if (sortBy.equalsIgnoreCase("year")) {
            return filmStorage.getDirectorFilmsByYear(id);
        }
        if (sortBy.equalsIgnoreCase("likes")) {
            return filmStorage.getDirectorFilmsByLikes(id);
        }
        throw new UnsupportedOperationException();
    }

    public List<Film> getCommonFilms(int id, int friendId) {
        return filmStorage.getCommonFilms(id, friendId);
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

        Set<Film> combinedFilms = new LinkedHashSet<>(filmsByTitle);
        combinedFilms.addAll(filmsByDirector);

        return new ArrayList<>(combinedFilms);
    }
}
