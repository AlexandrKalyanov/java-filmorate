package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mapper.MpaMapper;

import java.util.Collection;

@Repository
@Slf4j
@AllArgsConstructor
public class MpaStorageImpl implements MpaStorage{
    private final JdbcTemplate template;


    public Collection<Mpa> getAll(){
        return template.query("SELECT * FROM MPA_RATINGS ORDER BY MPA_RATING_ID", new MpaMapper());
    }

    public Mpa getMpaById(int id){
        return template.queryForObject("SELECT * FROM MPA_RATINGS WHERE MPA_RATING_ID = ?", new MpaMapper(),id);
    }

    public boolean existById(int id){
        SqlRowSet mpaRowSet = template.queryForRowSet("SELECT * FROM MPA_RATINGS WHERE MPA_RATING_ID =?", id);
        return mpaRowSet.next();
    }
}
