package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorStorage directorStorage;

    public Director create(Director director) {
        return directorStorage.create(director);
    }

    public Collection<Director> findAll() {
        return directorStorage.findAll();
    }

    public Director findById(long id) {
        return directorStorage.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Director № %d not found", id)));
    }

    public Director update(Director director) {
        isDirectorExist(director.getId());
        return directorStorage.update(director);
    }

    public void delete(long id) {
        isDirectorExist(id);
        directorStorage.delete(id);
    }

    public boolean isDirectorExist(long id) {
        return directorStorage.isDirectorExist(id);
    }
}