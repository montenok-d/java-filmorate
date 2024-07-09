package ru.yandex.practicum.filmorate.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class FilmRowMapper implements RowMapper<Film> {

    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Set<Genre> genres = new LinkedHashSet<>(genreStorage.findGenresByFilmId(rs.getLong("id")));
        Set<Director> directorSet = new HashSet<>();
        List<Director> directors = directorStorage.findDirectorsByFilmId(rs.getLong("id"));

        if (!CollectionUtils.isEmpty(directors)) {
            directorSet.addAll(directors);
        }

        return Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(Mpa.builder()
                        .id(rs.getLong("mpa_id"))
                        .name(rs.getString("mpa_name"))
                        .build())
                .genres(genres)
                .directors(directorSet)
                .build();
    }
}
