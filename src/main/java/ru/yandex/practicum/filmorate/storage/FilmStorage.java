package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    public Collection<Film> findAll();

    public Film create(Film film);

    public Film update(Film film);

    public void delete(long id);

    public Optional<Film> findFilmById(long id);

    public void addLike(long id, long userId);

    public void deleteLike(long id, long userId);

    public List<Film> getPopular(int count, Optional<Integer> genreId, Optional<Integer> year);

    List<Film> getDirectorFilmsByYear(Long directorId);

    List<Film> getDirectorFilmsByLikes(Long directorId);
}
