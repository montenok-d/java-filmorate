package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.FeedRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.Instant;
import java.util.*;

@Repository
@RequiredArgsConstructor
@Slf4j
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbc;
    private final UserRowMapper mapper;
    private final FeedRowMapper feedRowMapper;
    private static final String ADD_FEED = "INSERT INTO feed (entity_id, timestamp, user_id, event_type, operation) VALUES (?, ?, ?, ?, ?)";

    @Override
    public Collection<User> findAll() {
        String query = "SELECT * FROM users";
        return jdbc.query(query, mapper);
    }

    @Override
    public User create(User user) {
        String query = "INSERT INTO users (email, login, name, birthday) VALUES(?, ?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return user;
    }

    @Override
    public User update(User user) {
        String query = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? where id = ?";
        int rowsUpdated = jdbc.update(query, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        if (rowsUpdated > 0) {
            log.info("Данные User с Id {} обновлены.", user.getId());
        } else {
            log.info("Данные User с Id {} не удалось обновить.", user.getId());
        }
        return user;
    }

    @Override
    public void delete(long id) {
        String query = "DELETE FROM users WHERE id = ?";
        int rowsDeleted = jdbc.update(query, id);
        if (rowsDeleted > 0) {
            log.info("User с Id {} удалён.", id);
        } else {
            log.info("User с Id {} не удалён.", id);
        }
    }

    @Override
    public Optional<User> findUserById(long id) {
        String query = "SELECT * FROM users WHERE id = ?";
        try {
            User result = jdbc.queryForObject(query, mapper, id);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public void addFriend(long userId, long friendId) {
        String query = "INSERT into friends (user_id, friend_id, status) VALUES(?, ?, FALSE)";
        jdbc.update(query, userId, friendId);
        jdbc.update(ADD_FEED, friendId, Instant.now().toEpochMilli(), userId, "FRIEND", "ADD");
    }

    @Override
    public void deleteFriend(long id, long friendId) {
        String query = "DELETE FROM friends " +
                "WHERE user_id = ? AND friend_id = ?";
        jdbc.update(query, id, friendId);
        jdbc.update(ADD_FEED, friendId, Instant.now().toEpochMilli(), id, "FRIEND", "REMOVE");
    }

    @Override
    public List<User> findMutualFriends(long firstUserId, long secondUserId) {
        String query = "SELECT u.id, u.email, u.login, u.name, u.birthday " +
                "FROM users AS u " +
                "INNER JOIN friends f1 ON u.id = f1.friend_id " +
                "INNER JOIN friends f2 ON f1.friend_id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?;";
        return jdbc.query(query, mapper, firstUserId, secondUserId);
    }

    @Override
    public List<User> findAllFriends(long id) {
        String query = "SELECT * FROM users u " +
                "JOIN FRIENDS f ON u.ID = f.FRIEND_ID " +
                "WHERE f.USER_ID = ? " +
                "UNION SELECT * FROM users u " +
                "JOIN FRIENDS f2 ON u.ID = f2.user_id " +
                "WHERE f2.friend_id = ? AND STATUS = TRUE ";
        return jdbc.query(query, mapper, id, id);
    }

    @Override
    public List<Long> getUsersFilmsIds(Long userId) {
        String sql = "SELECT film_id FROM likes WHERE user_id = ?";
        return jdbc.query(sql, (rs, rowNum) -> rs.getLong("film_id"), userId);
    }

    @Override
    public List<Feed> getFeedByUserId(Long userId) {
        String query = "SELECT * FROM feed WHERE user_id = ?";
        return jdbc.query(query, feedRowMapper, userId);
    }
}
