package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;
    private final MpaService mpaService;

    public Collection<Film> findAll() {
        log.info("filmStorage.findAll");
        return filmStorage.findAll();
    }

    public Film create(Film film) {
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

    public List<Film> getPopular(int count) {
        return filmStorage.getPopular(count);
    }

}
