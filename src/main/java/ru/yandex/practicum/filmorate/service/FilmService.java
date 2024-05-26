package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    public Collection<Film> findAll() {
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
        if (userService.findUserById(userId) == null) {
            throw new EntityNotFoundException("User does not exist");
        }
        findFilmById(id).getLikes().add(userId);
    }

    public void deleteLike(long id, long userId) {
        Set<Long> likes = findFilmById(id).getLikes();
        if (!likes.contains(userId)) {
            throw new EntityNotFoundException(String.format("There wasn't like from user with id %d", userId));
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
