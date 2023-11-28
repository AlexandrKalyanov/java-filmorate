package ru.yandex.practicum.filmorate.storage.friendship;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;


@Repository
@AllArgsConstructor
public class FriendShipDbStorage implements FriendShipStorage {

    private final JdbcTemplate template;


    @Override
    public void add(int fromUserID, int toUserID, boolean isMutual) {
        template.update("INSERT INTO FRIENDSHIPS (FROM_USER_ID, TO_USER_ID, ISMUTUAL) "
                + "VALUES(?, ?, ?)", fromUserID, toUserID, isMutual);
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
                        "WHERE TO_USER_ID=?", this::mapRowFriendship, toUserId)
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
                this::mapRowFriendship,
                fromUserID, toUserID);
    }

    private Friendship mapRowFriendship(ResultSet rs, int rowNum) throws SQLException {
        Friendship friendship = new Friendship();
        friendship.setFromUserId(rs.getInt("from_user_id"));
        friendship.setToUserId(rs.getInt("to_user_id"));
        friendship.setIsMutual(rs.getBoolean("isMutual"));
        return friendship;
    }
}
