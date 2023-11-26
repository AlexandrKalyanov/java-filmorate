package ru.yandex.practicum.filmorate.storage.friendship;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.mapper.FriendshipMapper;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;


@Repository
@Slf4j
@AllArgsConstructor
public class FriendShipDbStorage implements FriendShipStorage {

    private final JdbcTemplate template;


    @Override
    public void add(int fromUserID, int toUserID, boolean isMutual) {
        template.update("INSERT INTO FRIENDSHIPS (FROM_USER_ID, TO_USER_ID, ISMUTUAL) "
                + "VALUES(?, ?, ?)", fromUserID, toUserID, isMutual);
        Friendship result = get(fromUserID, toUserID);
        log.trace("Добавлена связь: {}.", result);
    }


    @Override
    public void delete(int fromUserID, int toUserID) {
        Friendship result = Objects.requireNonNull(get(fromUserID, toUserID));
        template.update("DELETE FROM friendships "
                + "WHERE from_user_id=? "
                + "AND to_user_id=?", fromUserID, toUserID);
        if (result.getIsMutual()) {
            template.update("UPDATE friendships "
                    + "SET isMutual=false "
                    + "WHERE from_user_id=? "
                    + "AND to_user_id=?", toUserID, fromUserID);
        }
    }

    @Override
    public Collection<Integer> getFromUserIDs(int toUserId) {
        return template.query("SELECT FROM_USER_ID, TO_USER_ID, ISMUTUAL" +
                        " FROM FRIENDSHIPS " +
                        "WHERE TO_USER_ID=?", new FriendshipMapper(), toUserId)
                .stream()
                .map(Friendship::getFromUserId)
                .collect(Collectors.toList());
    }

    @Override
    public boolean contains(int fromID, int toID) {
        boolean result;
        try {
            get(fromID, toID);
            result = true;
        } catch (RuntimeException e) {
            result = false;
        }
        return result;
    }

    private Friendship get(long fromUserID, long toUserID) {
        return template.queryForObject("SELECT from_user_id, to_user_id, isMutual " +
                        "FROM friendships " +
                        "WHERE from_user_id=? AND to_user_id=?",
                new FriendshipMapper(),
                fromUserID, toUserID);
    }
}
