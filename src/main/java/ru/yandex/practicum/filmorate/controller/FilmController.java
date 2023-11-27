package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;


    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.debug("Incoming request to add a movie: {}", film);
        return filmService.add(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.debug("Incoming request to update a movie: {}", film);
        return filmService.update(film);
    }

    @GetMapping
    public Iterable<Film> getFilms() {
        log.debug("Incoming request to get all films");
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable int id) {
        log.debug("Incoming request to get film by id: {}", id);
        return filmService.get(id);
    }

    @GetMapping("/popular")
    public Iterable<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.debug("Incoming request to get popular fims: {}", count);
        return filmService.getPopularFilms(count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        log.debug("Incoming request to put like from user {} to film {}", userId, id);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        log.debug("Incoming request to delete like from user {} to film {}", userId, id);
        filmService.deleteLike(id, userId);
    }
}

