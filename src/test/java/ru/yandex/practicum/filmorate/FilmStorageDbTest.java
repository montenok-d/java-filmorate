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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = {FilmDbStorage.class, MpaDbStorage.class, FilmRowMapper.class, MpaRowMapper.class,
        GenreDbStorage.class, GenreRowMapper.class, MpaService.class, UserDbStorage.class, UserRowMapper.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Sql(scripts = {"/data.sql"})
class FilmStorageDbTest {

    private final FilmDbStorage filmDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final UserDbStorage userDbStorage;

    @Test
    void getFilmByIdTest() {
        Mpa mpa = mpaDbStorage.findMpaById(1L).get();
        Film film = newFilm("Film", "description", LocalDate.of(1998, 2, 23), 120, mpa);
        filmDbStorage.create(film);
        long filmId = film.getId();
        Optional<Film> filmFromDb = filmDbStorage.findFilmById(filmId);
        Assertions.assertThat(filmFromDb)
                .isPresent()
                .hasValueSatisfying(f ->
                        Assertions.assertThat(f).hasFieldOrPropertyWithValue("id", filmId))
                .hasValueSatisfying(f ->
                        Assertions.assertThat(f).hasFieldOrPropertyWithValue("name", "Film"))
                .hasValueSatisfying(f ->
                        Assertions.assertThat(f).hasFieldOrPropertyWithValue("description", "description"))
                .hasValueSatisfying(f ->
                        Assertions.assertThat(f).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1998, 2, 23)))
                .hasValueSatisfying(f ->
                        Assertions.assertThat(f).hasFieldOrPropertyWithValue("duration", 120));
    }

    @Test
    void updateFilmTest() {
        Mpa mpa = mpaDbStorage.findMpaById(1L).get();
        Film film = newFilm("Film", "description", LocalDate.of(1998, 2, 23), 120, mpa);
        filmDbStorage.create(film);
        long filmId = film.getId();
        Film newFilm = Film.builder()
                .id(filmId)
                .name("Film updated")
                .description("description updated")
                .duration(130)
                .releaseDate(LocalDate.of(2000, 2, 23))
                .mpa(mpa)
                .build();
        filmDbStorage.update(newFilm);
        Optional<Film> updatedFilm = filmDbStorage.findFilmById(filmId);
        Assertions.assertThat(updatedFilm)
                .isPresent()
                .hasValueSatisfying(f ->
                        Assertions.assertThat(f).hasFieldOrPropertyWithValue("id", filmId))
                .hasValueSatisfying(f ->
                        Assertions.assertThat(f).hasFieldOrPropertyWithValue("name", "Film updated"))
                .hasValueSatisfying(f ->
                        Assertions.assertThat(f).hasFieldOrPropertyWithValue("description", "description updated"))
                .hasValueSatisfying(f ->
                        Assertions.assertThat(f).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2000, 2, 23)))
                .hasValueSatisfying(f ->
                        Assertions.assertThat(f).hasFieldOrPropertyWithValue("duration", 130));
    }

    @Test
    void findAllFilmsTest() {
        Mpa mpa = mpaDbStorage.findMpaById(1L).get();
        Film film = newFilm("Film", "description", LocalDate.of(1998, 2, 23), 120, mpa);
        filmDbStorage.create(film);
        Collection<Film> allFilms = filmDbStorage.findAll();
        Assertions.assertThat(allFilms).isNotEmpty().isNotNull().doesNotHaveDuplicates();
        Assertions.assertThat(allFilms).extracting("description").contains(film.getDescription());
        Assertions.assertThat(allFilms).extracting("name").contains(film.getName());
        assertEquals(1, allFilms.size());
    }

    @Test
    void deleteFilmTest() {
        Mpa mpa = mpaDbStorage.findMpaById(1L).get();
        Film film = newFilm("Film", "description", LocalDate.of(1998, 2, 23), 120, mpa);
        filmDbStorage.create(film);
        int allFilmsSize = filmDbStorage.findAll().size();
        filmDbStorage.delete(film.getId());
        int newAllFilmsSize = filmDbStorage.findAll().size();
        assertEquals(allFilmsSize - 1, newAllFilmsSize);
    }

    @Test
    void getPopularFilmTest() {
        User user = User.builder()
                .email("mail@gmail.com")
                .login("New login")
                .name("New name")
                .birthday(LocalDate.of(1999, 12, 1))
                .build();
        Mpa mpa = mpaDbStorage.findMpaById(1L).get();
        Film film = newFilm("Best film", "description", LocalDate.of(1998, 2, 23), 120, mpa);
        filmDbStorage.create(film);
        userDbStorage.create(user);
        filmDbStorage.addLike(film.getId(), user.getId());
        List<Film> popularFilms = filmDbStorage.getPopular(10, Optional.empty(), Optional.empty());
        Assertions.assertThat(popularFilms).isNotEmpty().isNotNull().doesNotHaveDuplicates();
        Assertions.assertThat(popularFilms).extracting("description").contains(film.getDescription());
        Assertions.assertThat(popularFilms).extracting("name").contains(film.getName());
        assertThat(popularFilms.get(0)).hasFieldOrPropertyWithValue("name", "Best film");
    }

    private Film newFilm(
            String name,
            String description,
            LocalDate releaseDate,
            int duration,
            Mpa mpa) {
        return Film.builder()
                .name(name)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .mpa(mpa)
                .build();
    }
}