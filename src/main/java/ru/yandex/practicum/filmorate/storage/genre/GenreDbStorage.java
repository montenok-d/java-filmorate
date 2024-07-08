package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
            Genre result = jdbc.queryForObject("SELECT * FROM genres WHERE id = ?", mapper, id);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public List<Genre> findGenresByFilmId(Long id) {
        return jdbc.query("SELECT g.id, g.name FROM films_genres fg " +
                "JOIN genres g ON fg.genre_id = g.id WHERE fg.film_id = ? ORDER BY g.id", mapper, id);
    }
}