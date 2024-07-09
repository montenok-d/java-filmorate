package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    public Collection<Director> findAll() {
        log.info("GET /directors");
        return directorService.findAll();
    }

    @PostMapping
    public Director create(@Valid @RequestBody Director director) {
        log.info("POST /directors/{}", director.getName());
        return directorService.create(director);
    }

    @PutMapping
    public Director update(@Valid @RequestBody Director director) {
        log.info("PUT /directors/{}", director.getName());
        return directorService.update(director);
    }

    @GetMapping("/{id}")
    public Director findById(@PathVariable("id") Long id) {
        log.info("GET /directors/{}", id);
        return directorService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        log.info("DELETE /directors/{}", id);
        directorService.delete(id);
    }
}