package ru.yandex.practicum.filmorate.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeSrorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Objects;


@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final LikeSrorage likeSrorage;


    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, GenreStorage genreStorage, MpaStorage mpaStorage, LikeSrorage likeSrorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
        this.likeSrorage = likeSrorage;
    }

    public Film add(Film film) {
        checkFilmToAdd(film);
        Film result = filmStorage.add(film);
        filmStorage.addGenres(result.getId(), film.getGenres());
        result.setGenres(filmStorage.getGenres(result.getId()));
        result.setMpa(mpaStorage.getMpaById(result.getMpa().getId()));
        return result;
    }

    public Film update(Film film) {
        checkFilmToUpdate(film);
        Film result = filmStorage.update(film);
        filmStorage.updateGenres(result.getId(), film.getGenres());
        result.setGenres(filmStorage.getGenres(result.getId()));
        result.setMpa(mpaStorage.getMpaById(result.getMpa().getId()));
        return result;
    }

    public Film get(int filmID) {
        Film film = filmStorage.get(filmID);
        film.setGenres(filmStorage.getGenres(filmID));
        film.setMpa(mpaStorage.getMpaById(film.getMpa().getId()));
        return film;
    }

    public Collection<Film> getAll() {
        Collection<Film> films = filmStorage.getAll();
        for (Film film : films) {
            film.setGenres(filmStorage.getGenres(film.getId()));
            film.setMpa(mpaStorage.getMpaById(film.getMpa().getId()));
        }
        return films;
    }

    public Collection<Film> getPopularFilms(int count) {
        Collection<Mpa> mpa = mpaStorage.getAll();
        Collection<Film> films = filmStorage.getRating(count);
        for (Film film : films) {
            for (Mpa mpa1 : mpa) {
                if (Objects.equals(film.getMpa().getId(), mpa1.getId())) {
                    film.setMpa(mpa1);
                }
            }
        }
        return films;
    }

    public void addLike(int filmID, int userID) {
        checkLikeToAdd(filmID, userID);
        likeSrorage.add(filmID, userID);
    }

    public void deleteLike(int filmID, int userID) {
        checkLikeToDelete(filmID, userID);
        likeSrorage.delete(filmID, userID);
    }

    private int likeCompare(Film film, Film otherFilm) {
        return Integer.compare(likeSrorage.count(otherFilm.getId()), likeSrorage.count(film.getId()));
    }

    private void checkFilmToAdd(Film film) {

        if (film.getId() != 0) {
            if (filmStorage.contains(film.getId())) {
                throw new ValidateException("Не удалось добавить фильм");
            } else {
                throw new IllegalArgumentException("Запрещено устанавливать ID вручную");
            }
        }
        if (!mpaStorage.contains(film.getMpa().getId())) {
            throw new ObjectNotFoundException("MPA not found");
        }
        for (Genre genre : film.getGenres()) {
            if (!genreStorage.contains(genre.getId())) {
                throw new ObjectNotFoundException("Genre not found");
            }
        }
    }

    private void checkFilmToUpdate(Film film) {
        if (!filmStorage.contains(film.getId())) {
            throw new ObjectNotFoundException("Film not found");
        }
        if (!mpaStorage.contains(film.getMpa().getId())) {
            throw new ObjectNotFoundException("MPA not found");
        }
        for (Genre genre : film.getGenres()) {
            if (!genreStorage.contains(genre.getId())) {
                throw new ObjectNotFoundException("Genre not found");
            }
        }
    }

    private void checkLikeToAdd(int filmID, int userID) {
        if (!filmStorage.contains(filmID)) {
            throw new ObjectNotFoundException("Film not found");
        }
        if (!userStorage.contains(userID)) {
            throw new ObjectNotFoundException("User not found");
        }
        if (likeSrorage.contains(filmID, userID)) {
            throw new ValidateException("Like already exist");
        }
    }

    private void checkLikeToDelete(int filmID, int userID) {

        if (!filmStorage.contains(filmID)) {
            throw new ObjectNotFoundException("Film not found");
        }
        if (!userStorage.contains(userID)) {
            throw new ObjectNotFoundException("User not found");
        }
        if (!likeSrorage.contains(filmID, userID)) {
            throw new ObjectNotFoundException("Like not found");
        }
    }
}
