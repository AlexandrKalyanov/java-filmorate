package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Set;

public interface FilmStorage {

    Film add(Film film);

    Film update(Film film);

    Film get(int id);

    Collection<Film> getAll();

    Collection<Film> getRating(int count);

    boolean contains(int id);

    void addGenres(int id, Set<Genre> genres);

    void updateGenres(int id, Set<Genre> genres);

    Set<Genre> getGenres(int id);
}
