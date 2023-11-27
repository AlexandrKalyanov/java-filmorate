package ru.yandex.practicum.filmorate.storage.like;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.LikeMapper;

import java.util.Objects;


@Repository
@RequiredArgsConstructor
public class LikeDbStorage implements LikeSrorage {
    private final JdbcTemplate template;

    @Override
    public void add(int filmID, int userID) {
        template.update("INSERT INTO FILM_LIKES (FILM_ID, USER_ID) VALUES (?, ?)", filmID, userID);
    }

    @Override
    public void delete(int filmID, int userID) {
        template.update("DELETE FROM FILM_LIKES WHERE FILM_ID=? AND user_id=?", filmID, userID);
    }

    @Override
    public int count(int filmID) {
        return Objects.requireNonNull(
                template.queryForObject("SELECT COUNT(*) FROM FILM_LIKES WHERE FILM_ID=?", Integer.class, filmID));
    }

    @Override
    public boolean contains(int filmID, int userID) {
        try {
            template.queryForObject("SELECT FILM_ID, USER_ID FROM film_likes WHERE film_id=? AND user_id=?", new LikeMapper(), filmID, userID);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }
}
