package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class Film {
    long id;
    @NotBlank
    String name;
    @Size(max = 200)
    String description;
    @NotNull
    @DateTimeFormat
    LocalDate releaseDate;
    @Positive
    int duration;
}
