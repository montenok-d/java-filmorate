package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {
    public Collection<User> findAll();

    public User create(User user);

    public User update(User user);

    public void delete(long id);

    public Optional<User> findUserById(long id);

    public List<User> findAllFriends(long id);

    public void addFriend(long userId, long friendId);

    public void deleteFriend(long id, long friendId);

    public List<User> findMutualFriends(long firstUser, long secondUser);
}
