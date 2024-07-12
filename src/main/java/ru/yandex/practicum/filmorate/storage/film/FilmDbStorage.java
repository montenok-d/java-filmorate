package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.*;

@Repository
@RequiredArgsConstructor
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbc;
    private final FilmRowMapper mapper;
    private static final String ADD_FEED = "INSERT INTO feed (entity_id, timestamp, user_id, event_type, operation) VALUES (?, ?, ?, ?, ?)";

    @Override
    public Collection<Film> findAll() {
        String query = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                "f.mpa_id, m.name AS mpa_name " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.id ";
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
            ps.setLong(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        updateGenres(film);
        updateDirectors(film);
        long id = keyHolder.getKey().longValue();
        return findById(id).get();
    }

    @Override
    public Film update(Film film) {
        String query = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? where id = ?";
        jdbc.update(query, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        deleteGenres(film.getId());
        deleteDirectors(film.getId());
        updateGenres(film);
        updateDirectors(film);
        return findById(film.getId()).get();
    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM films WHERE id = ?";
        jdbc.update(query, id);
        deleteGenres(id);
    }

    @Override
    public Optional<Film> findById(Long id) {
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
    public void addLike(Long filmId, Long userId) {
        String query = "INSERT INTO likes (film_id, user_id) VALUES(?, ?)";
        jdbc.update(query, filmId, userId);
        jdbc.update(ADD_FEED, filmId, Instant.now().toEpochMilli(), userId, "LIKE", "ADD");
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        String query = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbc.update(query, filmId, userId);
        jdbc.update(ADD_FEED, filmId, Instant.now().toEpochMilli(), userId, "LIKE", "REMOVE");
    }

    @Override
    public List<Film> getPopular(Integer count, Optional<Integer> genreId, Optional<Integer> year) {
        List<Object> params = new ArrayList<>();

        String query = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name AS mpa_name, " +
                "COUNT(l.user_id) AS count " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.id " +
                "LEFT JOIN likes l ON f.id = l.film_id " +
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
                "ORDER BY count DESC " +
                "LIMIT ? ";
        params.add(count);
        return jdbc.query(query, mapper, params.toArray());
    }

    @Override
    public List<Film> getDirectorFilmsByYear(Long directorId) {
        String sql = "SELECT DISTINCT ON(FILMS.ID) FILMS.*, MPA.name AS mpa_name " +
                "FROM FILMS " +
                "LEFT JOIN FILMS_DIRECTORS FD ON FILMS.ID = FD.FILM_ID " +
                "LEFT JOIN DIRECTORS D ON D.ID = FD.DIRECTOR_ID " +
                "INNER JOIN MPA ON FILMS.MPA_ID = MPA.ID " +
                "LEFT JOIN LIKES ON FILMS.ID = LIKES.FILM_ID " +
                "WHERE D.ID = ? " +
                "ORDER BY FILMS.RELEASE_DATE ";
        return jdbc.query(sql, mapper, directorId);
    }

    @Override
    public List<Film> getDirectorFilmsByLikes(Long directorId) {
        String sql = "SELECT DISTINCT ON(FILMS.ID) FILMS.*, MPA.name AS mpa_name, count(LIKES.FILM_ID) as CNT " +
                "FROM FILMS " +
                "LEFT JOIN FILMS_DIRECTORS FD ON FILMS.ID = FD.FILM_ID " +
                "LEFT JOIN DIRECTORS D ON D.ID = FD.DIRECTOR_ID " +
                "INNER JOIN MPA ON FILMS.MPA_ID = MPA.ID " +
                "LEFT JOIN LIKES ON FILMS.ID = LIKES.FILM_ID " +
                "WHERE D.ID = ? " +
                "GROUP BY FILMS.ID " +
                "ORDER BY CNT DESC ";
        return jdbc.query(sql, mapper, directorId);
    }

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        String sql = "SELECT * FROM (SELECT f.*, count(fl.film_id) likes, m.name AS mpa_name FROM films f " +
                "LEFT JOIN likes fl on f.id = fl.film_id " +
                "LEFT JOIN mpa m ON f.mpa_id = m.id " +
                "GROUP BY f.id) f, likes l1, likes l2 " +
                "WHERE f.id = l1.film_id AND f.id = l2.film_id AND l1.user_id = ? AND l2.user_id = ? " +
                "ORDER BY likes DESC";
        return jdbc.query(sql, mapper, userId, friendId);
    }

    @Override
    public List<Film> searchByTitle(String query) {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name AS mpa_name " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.id " +
                "WHERE LOWER(f.name) LIKE LOWER(?)";
        return jdbc.query(sql, mapper, "%" + query + "%");
    }

    @Override
    public List<Film> searchByDirector(String query) {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name AS mpa_name " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.id " +
                "LEFT JOIN films_directors fd ON f.id = fd.film_id " +
                "LEFT JOIN directors d ON fd.director_id = d.id " +
                "WHERE LOWER(d.name) LIKE LOWER(?)";
        return jdbc.query(sql, mapper, "%" + query + "%");
    }

    private void deleteGenres(Long id) {
        String query = "DELETE FROM films_genres WHERE film_id = ?";
        jdbc.update(query, id);
    }

    private void deleteDirectors(Long id) {
        String deleteSql = "DELETE FROM films_directors WHERE film_id = ?";
        jdbc.update(deleteSql, id);
    }

    private void updateGenres(Film film) {
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
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

    private void updateDirectors(Film film) {
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            String sql = "INSERT INTO films_directors (film_id, director_id) VALUES (?, ?)";
            jdbc.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    List<Director> directors = new ArrayList<>(film.getDirectors());
                    ps.setLong(1, film.getId());
                    ps.setLong(2, directors.get(i).getId());
                }

                @Override
                public int getBatchSize() {
                    return film.getDirectors().size();
                }
            });
        }
    }
}