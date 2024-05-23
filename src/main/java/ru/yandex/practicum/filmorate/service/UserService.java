package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
        return userStorage.update(user);
    }

    public void delete(int id) {
        userStorage.delete(id);
    }

    public User findUserById(int id) {
        return userStorage.findUserById(id);
    }

    public void addFriend(int id, int friendId) {
        User user = findUserById(id);
        User friend = findUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(id);
    }

    public void deleteFriend(int id, int friendId) {
        User user = findUserById(id);
        User friend = findUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
    }

    public List<User> findAllFriends(int id) {
        User user = findUserById(id);
        List<User> friendsList = new ArrayList<>();
        Set<Integer> friendsIds = user.getFriends();
        if (friendsIds != null) {
            for (Integer friend : friendsIds) {
                friendsList.add(findUserById(friend));
            }
        }
        return friendsList;
    }

    public List<User> findMutualFriends(int firstUser, int secondUser) {
        List<User> firstUserFriends = findAllFriends(firstUser);
        List<User> secondUserFriends = findAllFriends(secondUser);
        firstUserFriends.retainAll(secondUserFriends);
        return firstUserFriends;
    }
}
