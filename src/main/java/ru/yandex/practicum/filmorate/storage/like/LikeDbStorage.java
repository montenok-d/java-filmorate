package ru.yandex.practicum.filmorate.storage.like;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.LikeRowMapper;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbc;
    private final LikeRowMapper mapper;

    @Override
    public List<Like> getLikesByFilmId(Long filmId) {
        String sql = "SELECT * FROM likes WHERE film_id = ?";
        return jdbc.query(sql, mapper, filmId);
    }
}