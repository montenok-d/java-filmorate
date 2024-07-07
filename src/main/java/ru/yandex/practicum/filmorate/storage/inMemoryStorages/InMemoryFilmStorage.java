package ru.yandex.practicum.filmorate.storage.inMemoryStorages;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Qualifier("InMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

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
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void delete(long id) {
        films.remove(id);
    }

    @Override
    public Optional<Film> findFilmById(long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public void addLike(long id, long userId) {

    }

    @Override
    public void deleteLike(long id, long userId) {

    }

    @Override
    public List<Film> getPopular(int count, Optional<Integer> genreId, Optional<Integer> year) {
        return null;
    }

    @Override
    public List<Film> getDirectorFilmsByYear(Long directorId) {
        return null;
    }

    @Override
    public List<Film> getDirectorFilmsByLikes(Long directorId) {
        return null;
    }

    @Override
    public List<Film> getCommonFilms(int id, int friendId) {
        return null;
    }

    @Override
    public List<Film> searchByTitle(String query) {
        String lowerCaseQuery = query.toLowerCase();
        return films.values().stream()
                .filter(film -> film.getName().toLowerCase().contains(lowerCaseQuery))
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> searchByDirector(String query) {
        String lowerCaseQuery = query.toLowerCase();
        return films.values().stream()
                .filter(film -> film.getDirectors().stream()
                        .anyMatch(director -> director.getName().toLowerCase().contains(lowerCaseQuery)))
                .collect(Collectors.toList());
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
