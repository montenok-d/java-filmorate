package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DirectorDbStorageTest {

    private final DirectorDbStorage directorDbStorage;

    private Director director1;
    private Director director2;

    @BeforeEach
    void setUp() {
        director1 = Director.builder()
                .id(1L)
                .name("First Test Director")
                .build();
        director2 = Director.builder()
                .id(2L)
                .name("Second Test Director")
                .build();
    }

    @Test
    void findAll() {
        directorDbStorage.create(director1);
        directorDbStorage.create(director2);
        List<Director> allDirectors = (List<Director>) directorDbStorage.findAll();
        Assertions.assertTrue(allDirectors.contains(director1) && allDirectors.contains(director2));
    }

    @Test
    void findById() {
        directorDbStorage.create(director1);
        Optional<Director> foundDirector = directorDbStorage.findById(director1.getId());
        Assertions.assertTrue(foundDirector.isPresent());
        Assertions.assertEquals(director1, foundDirector.get());
    }

    @Test
    void create() {
        Director createdDirector = directorDbStorage.create(director1);
        Assertions.assertNotNull(createdDirector);
        Assertions.assertEquals(director1.getName(), createdDirector.getName());
    }

    @Test
    void update() {
        Director createdDirector = directorDbStorage.create(director1);
        createdDirector.setName("Updated Test Director");
        Director updatedDirector = directorDbStorage.update(createdDirector);
        Assertions.assertNotNull(updatedDirector);
        Assertions.assertEquals("Updated Test Director", updatedDirector.getName());
    }

    @Test
    void delete() {
        directorDbStorage.create(director1);
        directorDbStorage.delete(director1.getId());
        Optional<Director> foundDirector = directorDbStorage.findById(director1.getId());
        Assertions.assertFalse(foundDirector.isPresent());
    }
}