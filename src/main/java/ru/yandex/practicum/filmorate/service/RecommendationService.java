package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final UserService userService;
    private final FilmService filmService;

    public Set<Film> findRecommendations(Long userId) {
        Map<Long, List<Long>> usersFilms = new HashMap<>();
        Collection<User> allUsers = userService.findAll();
        for (User user : allUsers) {
            usersFilms.put(user.getId(), userService.getUsersFilms(user.getId()));
        }
        long countMatches = 0;
        Long mostCloseUser = null;
        for (Long otherUserId : usersFilms.keySet()) {
            if (otherUserId == userId) continue;
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
            return userService.getUsersFilms(mostCloseUser).stream()
                    .filter(filmId -> !usersFilms.get(userId).contains(filmId))
                    .map(filmService::findById)
                    .collect(Collectors.toSet());
        }
    }
}