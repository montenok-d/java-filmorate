package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public void delete(int id) {
        filmStorage.delete(id);
    }

    public Film findFilmById(int id) {
        return filmStorage.findFilmById(id);
    }

    public void addLike(int id, int userId) {
        if (userStorage.findUserById(userId) != null) {
            findFilmById(id).getLikes().add(userId);
        }
    }

    public void deleteLike(int id, int userId) {
        Set<Integer> likes = findFilmById(id).getLikes();
        if (!likes.contains(userId)) {
            throw new EntityNotFoundException("User does not exist");
        }
        likes.remove(userId);
    }

    public List<Film> getPopular(int count) {
        return filmStorage.findAll().stream()
                .sorted((o1, o2) -> Integer.compare(o2.getLikes().size(),o1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
