package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else {
            throw new FilmNotFoundException("Film does not exist");
        }
        return film;
    }

    @Override
    public void delete(int id) {
        if (films.containsKey(id)) {
            films.remove(id);
        } else {
            throw new FilmNotFoundException("Film does not exist");
        }
    }

    @Override
    public Film findFilmById(int id) {
        return films.values()
                .stream()
                .filter(film -> film.getId() == id)
                .findFirst()
                .orElseThrow(() -> new FilmNotFoundException(String.format("Film № %d не найден", id)));
    }

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
