package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

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
        return userStorage.create(user);
    }

    public User update(User user) {
        findUserById(user.getId());
        return userStorage.update(user);
    }

    public void delete(long id) {
        findUserById(id);
        userStorage.delete(id);
    }

    public User findUserById(long id) {
        return userStorage.findUserById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User № %d не найден", id)));
    }

    public void addFriend(long id, long friendId) {
        User user = findUserById(id);
        User friend = findUserById(friendId);
        userStorage.addFriend(id, friendId);
    }

    public void deleteFriend(long id, long friendId) {
        User user = findUserById(id);
        User friend = findUserById(friendId);
        userStorage.deleteFriend(id, friendId);
    }

    public List<User> findAllFriends(long id) {
        User user = findUserById(id);
        return userStorage.findAllFriends(id);
    }

    public List<User> findMutualFriends(long firstUser, long secondUser) {
        return userStorage.findMutualFriends(firstUser, secondUser);
    }
}
