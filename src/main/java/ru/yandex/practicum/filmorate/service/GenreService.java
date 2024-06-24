package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreDbStorage genreDbStorage;

    public Collection<Genre> findAll() {
        return genreDbStorage.findAll();
    }

    public Genre findGenreById(long id) {
        return genreDbStorage.findGenreById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Genre № %d not found", id)));
    }

    public Genre checkGenreById(long id) {
        return genreDbStorage.findGenreById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(400),String.format("Genre № %d not found", id)));
    }
}
