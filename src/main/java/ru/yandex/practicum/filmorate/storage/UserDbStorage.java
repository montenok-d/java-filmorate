package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Repository
@Primary
@RequiredArgsConstructor
@Slf4j
public class UserDbStorage implements UserStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM users WHERE id = ?";
    private static final String UPDATE_BY_ID_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? where id = ?";
    private static final String INSERT_NEW_QUERY = "INSERT INTO users (email, login, name, birthday) VALUES(?, ?, ?, ?)";
    private static final String FIND_FRIENDS_QUERY = "SELECT id FROM users u JOIN FRIENDS f ON u.ID = f.FRIEND_ID WHERE f.USER_ID = ? UNION SELECT id FROM users u JOIN FRIENDS f2 ON u.ID = f2.user_id WHERE f2.friend_id = ? AND STATUS = TRUE ";

    private final JdbcTemplate jdbc;
    private final UserRowMapper mapper;

    public Collection<User> findAll() {
        return jdbc.query(FIND_ALL_QUERY, mapper);
    }

    @Override
    public User create(User user) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_NEW_QUERY, Statement.RETURN_GENERATED_KEYS);
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
        int rowsUpdated = jdbc.update(UPDATE_BY_ID_QUERY, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        if (rowsUpdated > 0) {
            log.info("Данные User с Id {} обновлены.", user.getId());
        } else {
            log.info("Данные User с Id {} не удалось обновить.", user.getId());
        }
        return user;
    }

    @Override
    public void delete(long id) {
        int rowsDeleted = jdbc.update(DELETE_BY_ID_QUERY, id);
        if (rowsDeleted > 0) {
            log.info("User с Id {} удалён.", id);
        } else {
            log.info("User с Id {} не удалён.", id);
        }
    }

    @Override
    public Optional<User> findUserById(long id) {
        try {
            User result = jdbc.queryForObject(FIND_BY_ID_QUERY, mapper, id);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    public void addFriend(long id, long friendId) {


    }

    public List<User> findAllFriends(long id) {
        return jdbc.query(FIND_FRIENDS_QUERY, mapper, id, id);
    }

}
