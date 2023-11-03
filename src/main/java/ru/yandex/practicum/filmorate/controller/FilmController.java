package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private final Logger log = LoggerFactory.getLogger(FilmController.class);

    @GetMapping
    public List<Film> findAll() {
        log.debug("Storage size films is {}", films.size());
        return new ArrayList<>(films.values());

    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        check(film);
        films.put(film.getId(), film);
        log.info("Add new film {}", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        check(film);
        films.put(film.getId(), film);
        log.info("Update film {}", film);
        return film;
    }

    private void check(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.debug("Film name is empty. Film {}", film);
            throw new ValidateException("Не заполнено наименование фильма");
        }
        if (film.getDescription().length() > 200) {
            log.debug("Film name is empty");
            throw new ValidateException("Максимальное кол-во символов в описании превысило 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1985, 12, 28))) {
            log.debug("Release date is before 28.12.1985. Film date: {}", film.getReleaseDate());
            throw new ValidateException("Дата релиза должна быть не раньще не раньше 28 декабря 1895 года");
        }
        if (film.getDuration().isNegative()) {
            log.debug("Duratino is negative: Duration: {}", film.getDuration());
            throw new ValidateException("Продолжительность фильма должна быть положительной");
        }
    }
}

