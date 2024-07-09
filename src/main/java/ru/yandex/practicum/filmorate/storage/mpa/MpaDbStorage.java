package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbc;
    private final MpaRowMapper mapper;

    @Override
    public Collection<Mpa> findAll() {
        String query = "SELECT * FROM mpa";
        return jdbc.query(query, mapper);
    }

    @Override
    public Optional<Mpa> findById(Long id) {
        try {
            String query = "SELECT * FROM mpa WHERE id = ?";
            Mpa result = jdbc.queryForObject(query, mapper, id);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }
}