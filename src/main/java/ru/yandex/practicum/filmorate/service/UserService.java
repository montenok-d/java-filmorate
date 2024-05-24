package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

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
        user.getFriends().add(friendId);
        friend.getFriends().add(id);
    }

    public void deleteFriend(long id, long friendId) {
        User user = findUserById(id);
        User friend = findUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
    }

    public List<User> findAllFriends(long id) {
        User user = findUserById(id);
        List<User> friendsList = new ArrayList<>();
        Set<Long> friendsIds = user.getFriends();
        if (friendsIds != null) {
            for (Long friend : friendsIds) {
                friendsList.add(findUserById(friend));
            }
        }
        return friendsList;
    }

    public List<User> findMutualFriends(long firstUser, long secondUser) {
        List<User> firstUserFriends = findAllFriends(firstUser);
        List<User> secondUserFriends = findAllFriends(secondUser);
        firstUserFriends.retainAll(secondUserFriends);
        return firstUserFriends;
    }
}
