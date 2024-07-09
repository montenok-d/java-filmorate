package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        validateName(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        findById(user.getId());
        return userStorage.update(user);
    }

    public void delete(Long id) {
        findById(id);
        userStorage.delete(id);
    }

    public User findById(Long id) {
        return userStorage.findUserById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User № %d not found", id)));
    }

    public void addFriend(Long userId, Long friendId) {
        findById(userId);
        findById(friendId);
        userStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        findById(userId);
        findById(friendId);
        userStorage.deleteFriend(userId, friendId);
    }

    public List<User> findAllFriends(Long userId) {
        findById(userId);
        return userStorage.findAllFriends(userId);
    }

    public List<User> findMutualFriends(Long firstUser, Long secondUser) {
        return userStorage.findMutualFriends(firstUser, secondUser);
    }

    public List<Feed> getFeedByUserId(Long userId) {
        findById(userId);
        return userStorage.getFeedByUserId(userId)
                .stream()
                .sorted(Comparator.comparing(Feed::getTimestamp))
                .toList();
    }

    private void validateName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}