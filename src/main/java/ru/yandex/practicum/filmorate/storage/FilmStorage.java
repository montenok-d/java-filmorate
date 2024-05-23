package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    public Collection<Film> findAll();

    public Film create(Film film);

    public Film update(Film film);

    public void delete(int id);

    public Film findFilmById(int id);
}
