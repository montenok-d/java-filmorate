package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbc;
    private final GenreRowMapper mapper;

    @Override
    public Collection<Genre> findAll() {
        String sql = "SELECT * FROM genres";
        return jdbc.query(sql, mapper);
    }

    @Override
    public Optional<Genre> findById(Long id) {
        try {
            String query = "SELECT * FROM genres WHERE id = ?";
            Genre result = jdbc.queryForObject(query, mapper, id);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public Set<Genre> findGenresByFilmId(Long id) {
        String query = "SELECT g.id, g.name FROM films_genres fg " +
                "JOIN genres g ON fg.genre_id = g.id WHERE fg.film_id = ? ORDER BY g.id";
        return new LinkedHashSet<>(jdbc.query(query, mapper, id));
    }
}