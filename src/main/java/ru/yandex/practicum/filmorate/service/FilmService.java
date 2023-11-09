package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    InMemoryFilmStorage inMemoryFilmStorage;
    private static final int MAX_DESCR_VALUE_SIZE = 200;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private static final int MAX_QUANTITY_POPULAR_FILMS = 10;
    private final Logger log = LoggerFactory.getLogger(FilmService.class);
    private int id = 0;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

    public Collection<Film> getAll() {
        Collection<Film> users = inMemoryFilmStorage.getAllFilms();
        log.debug("Storage size users is {}", users.size());
        return users;
    }

    public Film getFilmById(int id) {
        if (!inMemoryFilmStorage.getFilms().containsKey(id)) {
            throw new ObjectNotFoundException("Film not found");
        }
        return inMemoryFilmStorage.getFilmById(id);
    }

    public Film create(Film film) {
        check(film);
        if (film.getId() == 0) {
            film.setId(generateID());
        }
        inMemoryFilmStorage.addFilm(film);
        log.info("Add new film {}", film);
        return film;
    }

    public Film update(Film film) {
        check(film);
        if (!inMemoryFilmStorage.getFilms().containsKey(film.getId())) {
            throw new ObjectNotFoundException("film not found");
        }
        inMemoryFilmStorage.updateFilm(film);
        log.info("Update film {}", film);
        return film;
    }

    public Film addLike(int id, int userId) {
        if (!inMemoryFilmStorage.getFilms().containsKey(id)) {
            throw new ObjectNotFoundException("film not found");
        }
        return inMemoryFilmStorage.addLike(id, userId);

    }

    public Film deleteLike(int id, int userID) {
        if (!inMemoryFilmStorage.getFilms().containsKey(id)) {
            throw new ObjectNotFoundException("film not found");
        }
        if (userID < 0) {
            throw new ObjectNotFoundException("incorrect userId");
        }

        return inMemoryFilmStorage.deleteLike(id, userID);
    }

    public Collection<Film> topFilmsWithCount(Optional<Integer> count) {
        Comparator<Film> filmLikeComparator = (film1, film2) -> {
            if (film1.getLikes().size() == film2.getLikes().size()) {
                return (film1.getId() - film2.getId());
            } else {
                return film1.getLikes().size() - film2.getLikes().size();
            }
        };
        Set<Film> popularFilms = new TreeSet<>(filmLikeComparator.reversed());
        Collection<Film> films = inMemoryFilmStorage.getAllFilms();
        popularFilms.addAll(films);
        return popularFilms.stream().limit(count.orElse(MAX_QUANTITY_POPULAR_FILMS)).collect(Collectors.toSet());
    }

    private void check(Film film) {
        if (film.getName().isBlank()) {
            log.debug("Film name is empty. Film {}", film);
            throw new ValidateException("Не заполнено наименование фильма");
        }
        if (film.getDescription().length() > MAX_DESCR_VALUE_SIZE) {
            log.debug("Film name is empty");
            throw new ValidateException("Максимальное кол-во символов в описании превысило 200 символов");
        }
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.debug("Release date is before 28.12.1985. Film date: {}", film.getReleaseDate());
            throw new ValidateException("Дата релиза должна быть не раньще не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            log.debug("Duratino is negative: Duration: {}", film.getDuration());
            throw new ValidateException("Продолжительность фильма должна быть положительной");
        }
    }

    private int generateID() {
        return ++this.id;
    }

}
