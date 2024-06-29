package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

@Repository
@RequiredArgsConstructor
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbc;
    private final FilmRowMapper mapper;

    @Override
    public Collection<Film> findAll() {
        String query = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                "f.mpa_id, m.name AS mpa_name, fg.genre_id, g.name AS genre_name " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.id " +
                "LEFT JOIN films_genres fg ON fg.film_id = f.id " +
                "LEFT JOIN genres g ON fg.genre_id = g.id;";
        return jdbc.query(query, mapper);
    }

    @Override
    public Film create(Film film) {
        String query = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES(?, ?, ?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, Integer.valueOf((int) film.getMpa().getId()));
            return ps;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        updateGenres(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        String query = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ? where id = ?";
        jdbc.update(query, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        updateGenres(film);
        return film;
    }

    @Override
    public void delete(long id) {
        String query = "DELETE FROM films WHERE id = ?";
        jdbc.update(query, id);
    }

    @Override
    public Optional<Film> findFilmById(long id) {
        String query = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                "m.id AS mpa_id, m.name AS mpa_name, " +
                "FROM films AS f " +
                "LEFT JOIN mpa AS m ON (f.mpa_id = m.id) " +
                "WHERE f.id = ?;";
        try {
            Film result = jdbc.queryForObject(query, mapper, id);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public void addLike(long filmId, long userId) {
        String query = "INSERT INTO likes (film_id, user_id) VALUES(?, ?)";
        jdbc.update(query, filmId, userId);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        String query = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbc.update(query, filmId, userId);
    }

    @Override
    public List<Film> getPopular(int count, Optional<Integer> genreId, Optional<Integer> year) {
        List<Object> params = new ArrayList<>();
        String query = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name AS mpa_name, " +
                "count(l.user_id) as count " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.id " +
                "JOIN likes l ON f.id = l.film_id " +
                "LEFT JOIN films_genres fg ON fg.film_id = f.id ";
        if (genreId.isPresent() || year.isPresent()) {
            query += "WHERE ";
            if (genreId.isPresent()) {
                query += "fg.genre_id = ? ";
                params.add(genreId.get());
            }
            if (year.isPresent()) {
                if (genreId.isPresent()) {
                    query += "AND ";
                }
                query += "YEAR(f.release_date) = ? ";
                params.add(year.get());
            }
        }
        query += "GROUP BY f.id " +
                "ORDER BY count(l.user_id) desc " +
                "LIMIT ? ";
        params.add(count);
        return jdbc.query(query, mapper, params.toArray());
    }

    private void updateGenres(Film film) {
        if (film.getGenres() != null) {
            String sql = "INSERT INTO films_genres (film_id, genre_id) VALUES(?, ?)";
            jdbc.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    List<Genre> genres = new ArrayList<>(film.getGenres());
                    ps.setLong(1, film.getId());
                    ps.setLong(2, genres.get(i).getId());
                }

                @Override
                public int getBatchSize() {
                    return film.getGenres().size();
                }
            });
        }
    }

}
