package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;

import java.util.Collection;


@Repository
@AllArgsConstructor
public class MpaStorageImpl implements MpaStorage {
    private final JdbcTemplate template;


    public Collection<Mpa> getAll() {
        return template.query("SELECT MPA_RATING_ID,NAME FROM MPA_RATINGS ORDER BY MPA_RATING_ID", new MpaMapper());
    }

    public Mpa getMpaById(int id) {
        return template.queryForObject("SELECT mpa_rating_id, name FROM mpa_ratings WHERE mpa_rating_id=?", new MpaMapper(), id);
    }

    public boolean contains(int id) {
        try {
            getMpaById(id);
            return true;
        } catch (EmptyResultDataAccessException ex) {
            return false;
        }
    }
}