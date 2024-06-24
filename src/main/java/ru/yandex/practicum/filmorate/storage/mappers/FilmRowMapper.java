package ru.yandex.practicum.filmorate.storage.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

@RequiredArgsConstructor
@Component
public class FilmRowMapper implements RowMapper<Film> {

    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final MpaService mpaService;

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(mpaService.findMpaById(rs.getInt("mpa_id")))
                .genres(new HashSet<>(genreDbStorage.findGenresByFilmId(rs.getLong("id"))))
                .build();
    }
}
