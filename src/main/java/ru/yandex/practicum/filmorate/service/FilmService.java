package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;

    public Collection<Film> findAll() {
        Collection<Film> allFilms = filmStorage.findAll();
        allFilms.forEach(this::addGenresAndDirectors);
        return allFilms;
    }

    public Film create(Film film) {
        checkMpaExists(film.getMpa().getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                checkGenreExists(genre.getId());
            }
        }
        film = filmStorage.create(film);
        addGenresAndDirectors(film);
        return film;
    }

    public Film update(Film film) {
        checkFilmExists(film.getId());
        film = filmStorage.update(film);
        addGenresAndDirectors(film);
        return film;
    }

    public void delete(Long id) {
        checkFilmExists(id);
        filmStorage.delete(id);
    }

    public Film findById(Long id) {
        checkFilmExists(id);
        Film film = filmStorage.findById(id).get();
        addGenresAndDirectors(film);
        return film;
    }

    public void addLike(Long filmId, Long userId) {
        checkFilmExists(filmId);
        checkUserExists(userId);
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        checkFilmExists(filmId);
        checkUserExists(userId);
        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getPopular(Integer count, Optional<Integer> genreId, Optional<Integer> year) {
        List<Film> popularFilms = filmStorage.getPopular(count, genreId, year);
        popularFilms.forEach(this::addGenresAndDirectors);
        return popularFilms;

    }

    public List<Film> getDirectorFilms(String sortBy, Long directorId) {
        checkDirectorExists(directorId);
        List<Film> films = new ArrayList<>();
        if (sortBy.equalsIgnoreCase("year")) {
            films = filmStorage.getDirectorFilmsByYear(directorId);
            films.forEach(this::addGenresAndDirectors);
            return films;
        } else if (sortBy.equalsIgnoreCase("likes")) {
            films = filmStorage.getDirectorFilmsByLikes(directorId);
            films.forEach(this::addGenresAndDirectors);
            return films;
        } else {
            throw new IllegalArgumentException("Unsupported sortBy option: " + sortBy);
        }
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        List<Film> commonFilms = filmStorage.getCommonFilms(userId, friendId);
        commonFilms.forEach(this::addGenresAndDirectors);
        return commonFilms;
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
        combinedFilms.forEach(this::addGenresAndDirectors);
        return combinedFilms;
    }

    private void checkFilmExists(Long id) {
        filmStorage.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Film № %d not found", id)));
    }

    private void checkUserExists(Long id) {
        userStorage.findUserById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User № %d not found", id)));
    }

    private void checkMpaExists(Long id) {
        mpaStorage.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Mpa № %d not found", id)));
    }

    private void checkGenreExists(Long id) {
        genreStorage.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Genre № %d not found", id)));
    }

    private void checkDirectorExists(Long id) {
        directorStorage.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Director № %d not found", id)));
    }

    private Film addGenresAndDirectors(Film film) {
        Set<Genre> genres = new LinkedHashSet<>(genreStorage.findGenresByFilmId(film.getId()));
        Set<Director> directors = new LinkedHashSet<>(directorStorage.findDirectorsByFilmId(film.getId()));
        film.setGenres(genres);
        film.setDirectors(directors);
        return film;
    }
}