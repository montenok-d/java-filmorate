package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.RecommendationService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RecommendationService recommendationService;

    @GetMapping
    public Collection<User> findAll() {
        log.info("GET /users");
        return userService.findAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("POST /user/{}", user.getName());
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("PUT / user / {}", user.getName());
        return userService.update(user);
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable("id") Long id) {
        log.info("GET /user/{}", id);
        return userService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        log.info("DELETE /user/{}", id);
        userService.delete(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Long userId,
                          @PathVariable("friendId") Long friendId) {
        log.info("PUT /{}/friends/{}", userId, friendId);
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") Long userId,
                             @PathVariable("friendId") Long friendId) {
        log.info("DELETE /{}/friends/{}", userId, friendId);
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> findAllFriends(@PathVariable("id") Long userId) {
        log.info("GET /{}/friends", userId);
        return userService.findAllFriends(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findMutualFriends(@PathVariable("id") Long firstUser,
                                        @PathVariable("otherId") Long secondUser) {
        log.info("PUT /{}/friends/common/{}", firstUser, secondUser);
        return userService.findMutualFriends(firstUser, secondUser);
    }

    @GetMapping("/{id}/recommendations")
    public Set<Film> findRecommendations(@PathVariable("id") Long userId) {
        log.info("GET /{}/recommendations", userId);
        return recommendationService.findRecommendations(userId);
    }

    @GetMapping("/{id}/feed")
    public List<Feed> getFeed(@PathVariable("id") Long userId) {
        log.info("GET /{}/feed", userId);
        return userService.getFeedByUserId(userId);
    }
}