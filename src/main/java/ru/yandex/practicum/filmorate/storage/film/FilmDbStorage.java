package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


@Repository
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate template;

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
                this::mapRowFilm,
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
        try {
            return template.queryForObject("SELECT FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION_IN_MINUTES, MPA_RATING_ID FROM FILMS "
                    + "WHERE FILM_ID=?", this::mapRowFilm, id);
        } catch (RuntimeException e) {
            throw new ObjectNotFoundException("Not found");
        }
    }

    @Override
    public Collection<Film> getAll() {
        return template.query("SELECT FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION_IN_MINUTES, MPA_RATING_ID "
                + "FROM FILMS", this::mapRowFilm);
    }

    @Override
    public Collection<Film> getRating(int count) {
        return template.query("SELECT films.*, COUNT(l.film_id) as count FROM films" +
                " LEFT JOIN FILM_LIKES l ON films.film_id=l.film_id " +
                "GROUP BY films.film_id " +
                "ORDER BY count DESC " +
                "LIMIT ?", this::mapRowFilm, count);
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
                        + "ORDER BY g.GENRE_ID", this::mapRowGenre, id));
    }

    private void delete(int filmID) {
        template.update("DELETE FROM film_genres WHERE film_id=?", filmID);
    }

    private Film mapRowFilm(ResultSet rs, int rowNum) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("mpa_rating_id"));
        Film film = new Film();
        film.setId(rs.getInt("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration_in_minutes"));
        film.setMpa(mpa);
        return film;
    }

    private Genre mapRowGenre(ResultSet rs, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(rs.getInt("genre_id"));
        genre.setName(rs.getString("name"));
        return genre;
    }

}
