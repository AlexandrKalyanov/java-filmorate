package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;


@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate template;

    @Override
    public Genre get(int genreID) {
        return template.queryForObject("SELECT GENRE_ID, NAME FROM GENRES WHERE GENRE_ID=?", new GenreMapper(), genreID);
    }

    @Override
    public Collection<Genre> getAll() {
        return template.query("SELECT genre_id, name " + "FROM genres " + "ORDER BY genre_id", new GenreMapper());
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
}
