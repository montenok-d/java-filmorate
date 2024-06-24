package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = {GenreDbStorage.class, GenreRowMapper.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Sql(scripts = {"/films.sql"})
public class GenreStorageDbTest {
    private final GenreDbStorage genreDbStorage;

    @Test
    void getMpaByIdTest() {
        Optional<Genre> genre = genreDbStorage.findGenreById(1L);
        Assertions.assertThat(genre)
                .isPresent()
                .hasValueSatisfying(g ->
                        Assertions.assertThat(g).hasFieldOrPropertyWithValue("id", 1L))
                .hasValueSatisfying(g ->
                        Assertions.assertThat(g).hasFieldOrPropertyWithValue("name", "Комедия"));
    }

    @Test
    void findAllTest() {
        List<Genre> genres = (List<Genre>) genreDbStorage.findAll();
        Assertions.assertThat(genres).isNotEmpty().isNotNull().doesNotHaveDuplicates();
        assertEquals(6, genres.size());
    }
}
