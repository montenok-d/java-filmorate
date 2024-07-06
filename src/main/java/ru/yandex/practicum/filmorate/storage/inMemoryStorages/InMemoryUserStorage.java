package ru.yandex.practicum.filmorate.storage.inMemoryStorages;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Component
@Qualifier("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        user.setId(getNextId());
        validateName(user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(long id) {
        users.remove(id);
    }

    @Override
    public Optional<User> findUserById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAllFriends(long id) {
        return null;
    }

    @Override
    public void addFriend(long userId, long friendId) {

    }

    @Override
    public void deleteFriend(long id, long friendId) {

    }

    @Override
    public List<User> findMutualFriends(long firstUser, long secondUser) {
        return null;
    }

    @Override
    public List<Long> getUsersFilmsIds(Long userId) {
        return null;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validateName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
