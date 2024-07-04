package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.annotation.ReleaseDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {
    private long id;
    @NotBlank(message = "name cannot be empty")
    private String name;
    @Size(max = 200)
    private String description;
    @NotNull(message = "release date cannot be empty")
    @DateTimeFormat
    @ReleaseDate(value = "1895-12-28", message = "Release date should be after December 28, 1895.")
    private LocalDate releaseDate;
    @Positive
    private int duration;
    private Set<Genre> genres = new HashSet<>();
    private Mpa mpa;
    private Set<Director> directors;
}
