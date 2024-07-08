package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DirectorStorage {

    Collection<Director> findAll();

    Optional<Director> findById(Long id);

    Director create(Director director);

    Director update(Director director);

    void delete(Long id);

    List<Director> findDirectorsByFilmId(Long id);
}