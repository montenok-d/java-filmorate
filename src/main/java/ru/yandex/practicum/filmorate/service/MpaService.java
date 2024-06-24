package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaDbStorage mpaDbStorage;

    public Collection<Mpa> findAll() {
        return mpaDbStorage.findAll();
    }

    public Mpa findMpaById(long id) {
        return mpaDbStorage.findMpaById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Mpa № %d not found", id)));
    }

    public Mpa checkMpaForFilm(long id) {
        return mpaDbStorage.findMpaById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(400), String.format("Mpa № %d not found", id)));
    }
}
