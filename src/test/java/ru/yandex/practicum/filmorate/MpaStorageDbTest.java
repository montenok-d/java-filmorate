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
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRowMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = {MpaDbStorage.class, MpaRowMapper.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Sql(scripts = {"/films.sql"})
public class MpaStorageDbTest {
    private final MpaDbStorage mpaDbStorage;

    @Test
    void getMpaByIdTest() {
        Optional<Mpa> mpa = mpaDbStorage.findMpaById(1L);
        Assertions.assertThat(mpa)
                .isPresent()
                .hasValueSatisfying(m ->
                        Assertions.assertThat(m).hasFieldOrPropertyWithValue("id", 1L))
                .hasValueSatisfying(m ->
                        Assertions.assertThat(m).hasFieldOrPropertyWithValue("name", "G"));
    }

    @Test
    void findAllTest() {
        List<Mpa> mpas = (List<Mpa>) mpaDbStorage.findAll();
        Assertions.assertThat(mpas).isNotEmpty().isNotNull().doesNotHaveDuplicates();
        assertEquals(5, mpas.size());
    }
}

