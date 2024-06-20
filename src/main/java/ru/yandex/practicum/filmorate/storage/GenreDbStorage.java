package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM genres";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE id = ?";
    private static final String FIND_GENRES_BY_FILM_ID_QUERY = "SELECT * FROM genres JOIN films_genres WHERE film_id = ?";

    private final JdbcTemplate jdbc;
    private final GenreRowMapper mapper;

    public Collection<Genre> findAll() {
        return jdbc.query(FIND_ALL_QUERY, mapper);
    }

    public Optional<Genre> findGenreById(long id) {
        try {
            Genre result = jdbc.queryForObject(FIND_BY_ID_QUERY, mapper, id);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    public List<Genre> findGenresByFilmId(long id) {
        return jdbc.query(FIND_GENRES_BY_FILM_ID_QUERY, mapper, id);
    }
}
