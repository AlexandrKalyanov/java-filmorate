package ru.yandex.practicum.filmorate.storage.friendship;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
@AllArgsConstructor
public class FriendShipStorage {

    private final JdbcTemplate template;
    public boolean existById(int fromId, int toId){
        SqlRowSet mpaRowSet = template.queryForRowSet("SELECT * FROM FRIENDSHIPS WHERE FROM_USER_ID = ? && TO_USER_ID = ?", fromId,toId);
        return mpaRowSet.next();
    }

    public void addFriend(int fromId, int toId){
        template.update("INSERT INTO FRIENDSHIPS Values (FROM_USER_ID =?,TO_USER_ID = ?, ISMUTUAL = 'false')",fromId,toId);
    }
}
