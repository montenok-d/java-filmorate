package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    void delete(long id);

    Optional<Film> findFilmById(long id);

    void addLike(long id, long userId);

    void deleteLike(long id, long userId);

    List<Film> getPopular(int count);

    List<Film> getDirectorFilmsByYear(Long directorId);

    List<Film> getDirectorFilmsByLikes(Long directorId);
}
