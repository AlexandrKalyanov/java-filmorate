package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Date;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


@Repository

@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {
    JdbcTemplate template;

    @Override
    public Film add(Film film) {
        template.update("INSERT INTO FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION_IN_MINUTES, MPA_RATING_ID) "
                        + "VALUES(?, ?, ?, ?, ?)",
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId());
        return template.queryForObject("SELECT film_id, name, description, release_date, duration_in_minutes, mpa_rating_id "
                        + "FROM FILMS "
                        + "WHERE NAME=? "
                        + "AND DESCRIPTION=? "
                        + "AND RELEASE_DATE=? "
                        + "AND DURATION_IN_MINUTES=?"
                        + "AND MPA_RATING_ID=?",
                new FilmMapper(),
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId());
    }

    @Override
    public Film update(Film film) {
        template.update("UPDATE FILMS "
                        + "SET NAME=?, DESCRIPTION=?, RELEASE_DATE=?, DURATION_IN_MINUTES=?, MPA_RATING_ID=? "
                        + "WHERE FILM_ID=?",
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        return get(film.getId());
    }

    @Override
    public Film get(int id) {
        return template.queryForObject("SELECT FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION_IN_MINUTES, MPA_RATING_ID FROM FILMS "
                + "WHERE FILM_ID=?", new FilmMapper(), id);
    }

    @Override
    public Collection<Film> getAll() {
        return template.query("SELECT FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION_IN_MINUTES, MPA_RATING_ID "
                + "FROM FILMS", new FilmMapper());
    }

    @Override
    public boolean contains(int id) {
        try {
            get(id);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    @Override
    public void addGenres(int filmID, Set<Genre> genres) {
        for (Genre genre : genres) {
            template.update("INSERT INTO FILM_GENRES (FILM_ID, GENRE_ID) "
                    + "VALUES (?, ?)", filmID, genre.getId());
        }
    }

    @Override
    public void updateGenres(int id, Set<Genre> genres) {
        delete(id);
        addGenres(id, genres);
    }

    @Override
    public Set<Genre> getGenres(int id) {
        return new HashSet<>(template.query(
                "SELECT f.GENRE_ID, g.NAME "
                        + "FROM FILM_GENRES AS f "
                        + "LEFT OUTER JOIN GENRES AS g ON f.GENRE_ID = g.GENRE_ID "
                        + "WHERE f.FILM_ID=? "
                        + "ORDER BY g.GENRE_ID", new GenreMapper(), id));
    }

    private void delete(int filmID) {
        template.update("DELETE FROM film_genres WHERE film_id=?", filmID);
    }

}
