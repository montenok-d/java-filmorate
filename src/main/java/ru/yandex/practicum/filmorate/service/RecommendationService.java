package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public Set<Film> findRecommendations(Long userId) {
        Map<Long, List<Long>> usersFilms = new HashMap<>();
        Collection<User> allUsers = userStorage.findAll();
        for (User user : allUsers) {
            usersFilms.put(user.getId(), userStorage.getUsersFilmsIds(user.getId()));
        }
        long countMatches = 0;
        Long mostCloseUser = null;
        for (Long otherUserId : usersFilms.keySet()) {
            if (Objects.equals(otherUserId, userId)) continue;
            long numberOfMatches = usersFilms.get(otherUserId).stream()
                    .filter(filmId -> usersFilms.get(userId).contains(filmId)).count();
            if (numberOfMatches == countMatches & numberOfMatches != 0) {
                mostCloseUser = otherUserId;
            } else if (numberOfMatches > countMatches) {
                countMatches = numberOfMatches;
                mostCloseUser = otherUserId;
            }
        }
        if (countMatches == 0) {
            return new HashSet<>();
        } else {
            return userStorage.getUsersFilmsIds(mostCloseUser).stream()
                    .filter(filmId -> !usersFilms.get(userId).contains(filmId))
                    .map(id -> filmStorage.findById(id).get())
                    .collect(Collectors.toSet());
        }
    }
}