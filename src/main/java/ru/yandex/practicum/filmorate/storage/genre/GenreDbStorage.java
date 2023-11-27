package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;


@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate template;

    @Override
    public Genre get(int genreID) {
        return template.queryForObject("SELECT GENRE_ID, NAME FROM GENRES WHERE GENRE_ID=?", this::mapRowGenre, genreID);
    }

    @Override
    public Collection<Genre> getAll() {
        return template.query("SELECT genre_id, name " + "FROM genres " + "ORDER BY genre_id", this::mapRowGenre);
    }

    @Override
    public boolean contains(int genreID) {
        try {
            get(genreID);
            return true;
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private Genre mapRowGenre(ResultSet rs, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(rs.getInt("genre_id"));
        genre.setName(rs.getString("name"));
        return genre;
    }
}
