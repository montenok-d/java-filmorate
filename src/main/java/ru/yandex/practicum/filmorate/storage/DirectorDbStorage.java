package ru.yandex.practicum.filmorate.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.mappers.DirectorRowMapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbc;
    private final DirectorRowMapper mapper;

    @Override
    public Collection<Director> findAll() {
        String sql = "SELECT * FROM directors ORDER BY id";
        return jdbc.query(sql, mapper);
    }

    @Override
    public Optional<Director> findById(long id) {
        try {
            Director result = jdbc.queryForObject("SELECT * FROM directors WHERE id = ? ORDER BY id", mapper, id);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public Director create(Director director) {
        SimpleJdbcInsert insertActor = new SimpleJdbcInsert(jdbc)
                .withTableName("directors")
                .usingGeneratedKeyColumns("id");

        Number id = insertActor.executeAndReturnKey(toMap(director));

        director.setId(id.longValue());
        return director;
    }

    @Override
    public Director update(Director director) {
        if (!isDirectorExist(director.getId())) {
            throw new EntityNotFoundException(String.format("Director with id=%d not found", director.getId()));
        }
        String sql = "UPDATE directors SET name = ? WHERE id = ?";
        jdbc.update(sql, director.getName(), director.getId());

        return director;
    }

    @Override
    public void delete(long directorId) {
        String deleteFilmDirectorsSql = "DELETE FROM films_directors WHERE director_id = ?";
        jdbc.update(deleteFilmDirectorsSql, directorId);

        String deleteDirectorSql = "DELETE FROM directors WHERE id = ?";
        jdbc.update(deleteDirectorSql, directorId);
    }

    @Override
    public boolean isDirectorExist(long id) {
        String sql = "SELECT COUNT(*) FROM directors WHERE id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    public List<Director> findDirectorsByFilmId(long id) {
        String sql = "SELECT d.id, d.name FROM films_directors fd " +
                "JOIN directors d ON fd.director_id = d.id WHERE fd.film_id = ? ORDER BY d.id";
        return jdbc.query(sql, mapper, id);
    }

    private Map<String, Object> toMap(Director director) {
        Map<String, Object> parameters;
        ObjectMapper objectMapper = new ObjectMapper();
        parameters = objectMapper.convertValue(director, new TypeReference<>() {
        });
        return parameters;
    }
}