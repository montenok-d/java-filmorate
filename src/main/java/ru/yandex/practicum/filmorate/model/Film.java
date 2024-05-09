package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
public class Film {
    private long id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @NotNull
    @DateTimeFormat
    private LocalDate releaseDate;
    @Positive
    private int duration;
}
