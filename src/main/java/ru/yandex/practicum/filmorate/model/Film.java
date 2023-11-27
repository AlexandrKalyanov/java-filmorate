package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.ReleaseDate;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
public class Film {
    private int id;
    @NotBlank
    private String name;
    @NotBlank
    @Size(min = 1, max = 200)
    private String description;
    @NotNull
    @ReleaseDate
    private LocalDate releaseDate;
    @NotNull
    @Positive
    private int duration;
    @JsonIgnore
    @Deprecated
    private Set<Integer> likes = new HashSet<>();
    @NotNull
    private Mpa mpa;
    private Set<Genre> genres = new HashSet<>();

}
