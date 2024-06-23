package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private static final String FIND_ALL_QUERY = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
            "f.mpa_id, m.name AS mpa_name, fg.genre_id, g.name AS genre_name " +
            "FROM films f " +
            "LEFT JOIN mpa m ON f.mpa_id = m.id " +
            "LEFT JOIN films_genres fg ON fg.film_id = f.id " +
            "LEFT JOIN genres g ON fg.genre_id = g.id;";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ? ";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM films WHERE id = ?";
    private static final String UPDATE_BY_ID_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ? where id = ?";
    private static final String INSERT_NEW_QUERY = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES(?, ?, ?, ?, ?)";
    private static final String FIND_POPULAR_QUERY = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, " +
            "count(l.user_id) as count FROM films f JOIN likes l ON f.id = l.film_id GROUP BY f.id ORDER BY count(l.user_id) desc LIMIT ? ";
    private static final String ADD_LIKE_QUERY = "INSERT INTO likes (film_id, user_id) VALUES(?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String ADD_FILM_GENRES_QUERY = "INSERT INTO films_genres (film_id, genre_id) VALUES(?, ?)";

    private final JdbcTemplate jdbc;
    private final FilmRowMapper mapper;

    @Override
    public Collection<Film> findAll() {
        return jdbc.query(FIND_ALL_QUERY, mapper);
    }

    @Override
    public Film create(Film film) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_NEW_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, Integer.valueOf((int) film.getMpa().getId()));
            return ps;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                jdbc.update(ADD_FILM_GENRES_QUERY, film.getId(), genre.getId());
            }
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        jdbc.update(UPDATE_BY_ID_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        return film;
    }

    @Override
    public void delete(long id) {
        jdbc.update(DELETE_BY_ID_QUERY, id);
    }

    @Override
    public Optional<Film> findFilmById(long id) {
        try {
            Film result = jdbc.queryForObject(FIND_BY_ID_QUERY, mapper, id);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public void addLike(long filmId, long userId) {
        jdbc.update(ADD_LIKE_QUERY, filmId, userId);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        jdbc.update(DELETE_LIKE_QUERY, filmId, userId);
    }

    @Override
    public List<Film> getPopular(int count) {
        List<Film> films = jdbc.query(FIND_POPULAR_QUERY, mapper, count);
        return films;
    }

}
