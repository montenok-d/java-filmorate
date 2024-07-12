package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    void delete(Long id);

    Optional<Film> findById(Long id);

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    List<Film> getPopular(Integer count, Optional<Integer> genreId, Optional<Integer> year);

    List<Film> getDirectorFilmsByYear(Long directorId);

    List<Film> getDirectorFilmsByLikes(Long directorId);

    List<Film> getCommonFilms(Long userId, Long friendId);

    List<Film> searchByTitle(String query);

    List<Film> searchByDirector(String query);
}