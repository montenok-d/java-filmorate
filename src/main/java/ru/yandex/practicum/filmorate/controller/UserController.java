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
        log.info("GET / users");
        return userService.findAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("POST / user / {}", user.getName());
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("PUT / user / {}", user.getName());
        return userService.update(user);
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable("id") int id) {
        log.info("GET /user/{}", id);
        return userService.findUserById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") int id) {
        log.info("DELETE /user/{}", id);
        userService.delete(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") int id,
                          @PathVariable("friendId") int friendId) {
        log.info("PUT /{}/friends/{}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") int id,
                             @PathVariable("friendId") int friendId) {
        log.info("DELETE /{}/friends/{}", id, friendId);
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> findAllFriends(@PathVariable("id") int id) {
        log.info("GET /{}/friends", id);
        return userService.findAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findMutualFriends(@PathVariable("id") int id,
                                        @PathVariable("otherId") int otherId) {
        log.info("PUT /{}/friends/common/{}", id, otherId);
        return userService.findMutualFriends(id, otherId);
    }

    @GetMapping("/{id}/recommendations")
    public Set<Film> findRecommendations(@PathVariable("id") long id) {
        log.info("GET /{}/recommendations", id);
        return recommendationService.findRecommendations(id);
    }

    @GetMapping("/{id}/feed")
    public List<Feed> getFeed(@PathVariable("id") long id) {
        log.info("GET /{}/feed", id);
        return userService.getFeedByUserId(id);
    }
}
