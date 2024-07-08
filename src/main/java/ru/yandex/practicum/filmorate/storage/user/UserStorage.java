package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Collection<User> findAll();

    User create(User user);

    User update(User user);

    void delete(Long id);

    Optional<User> findUserById(Long id);

    List<User> findAllFriends(Long id);

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long id, Long friendId);

    List<User> findMutualFriends(Long firstUser, Long secondUser);

    List<Long> getUsersFilmsIds(Long userId);

    List<Feed> getFeedByUserId(Long userId);
}