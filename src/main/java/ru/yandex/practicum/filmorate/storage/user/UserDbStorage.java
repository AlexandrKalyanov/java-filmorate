package ru.yandex.practicum.filmorate.storage.user;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;


@Repository
@AllArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate template;

    @Override
    public User addUser(User user) {
        template.update("INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY) "
                        + "VALUES (?, ?, ?, ?)",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()));
        return template.queryForObject("SELECT USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY FROM users WHERE email= ?", this::mapRowUser, user.getEmail());
    }

    @Override
    public int deleteUser(int id) {
        int row = template.update("DELETE FROM USERS where USER_ID = ? ", id);
        if (row == 0) {
            throw new ObjectNotFoundException("Object not found");
        }
        return id;
    }

    @Override
    public User updateUser(User user) {
        template.update("UPDATE USERS SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? WHERE USER_ID =? ",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId());
        return template.queryForObject("SELECT USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY FROM users WHERE USER_ID = ?", this::mapRowUser, user.getId());
    }

    @Override
    public User getUserById(int id) {
        return template.queryForObject("SELECT USER_ID, NAME, LOGIN, BIRTHDAY, EMAIL FROM users WHERE USER_ID = ?", this::mapRowUser, id);
    }

    @Override
    public Collection<User> getUsers() {
        return template.query("SELECT * FROM USERS", this::mapRowUser);
    }

    @Override
    public boolean contains(int id) {
        SqlRowSet userRows = template.queryForRowSet("SELECT * FROM USERS WHERE USER_ID = ?", id);
        return userRows.next();
    }

    @Override
    public User deleteFriend(int id, int friendId) {
        return null;
    }

    @Override
    public Collection<User> getMutualFriends(int id, int otherId) {
        return new ArrayList<>(template.query("SELECT * FROM USERS WHERE USER_ID IN(" +
                "SELECT FROM_USER_ID FROM FRIENDSHIPS WHERE TO_USER_ID = ? && ISMUTUAL = 'true') " +
                "AND USER_ID IN(SELECT TO_USER_ID FROM FRIENDSHIPS WHERE USER_ID = ? && ISMUTUAL = 'true')", this::mapRowUser, id, otherId));
    }

    @Override
    public User addFriend(int id, int friendId) {
        User user = getUserById(id);
        try {
            getUserById(id);
            getUserById(friendId);
        } catch (RuntimeException e) {
            throw new ObjectNotFoundException("User not found");
        }
        SqlRowSet friendshipRowsIdToFriend = template.queryForRowSet("SELECT * FROM FRIENDSHIPS WHERE FROM_USER_ID = ? AND TO_USER_ID = ?", id, friendId);
        SqlRowSet friendshipRowsFriendToId = template.queryForRowSet("SELECT * FROM FRIENDSHIPS WHERE FROM_USER_ID = ? AND TO_USER_ID = ?", friendId, id);
        String sqlQueryTrue = "INSERT INTO FRIENDSHIPS (FROM_USER_ID, TO_USER_ID,ISMUTUAL) VALUES(?, ?, true)";
        if (friendshipRowsIdToFriend.next()) {
            template.update(sqlQueryTrue, id, friendId);
            return user;
        }
        if (friendshipRowsFriendToId.next()) {
            template.update(sqlQueryTrue, friendId, id);
            return user;
        }
        String sqlQueryFalse = "INSERT INTO FRIENDSHIPS (FROM_USER_ID, TO_USER_ID,ISMUTUAL) VALUES(?, ?, false)";
        template.update(sqlQueryFalse, id, friendId);
        return user;
    }

    @Override
    public Collection<User> getFriendsByUserId(int id) {
        String sqlQuery = "SELECT user_id, email, login, name, birthday FROM users WHERE user_id IN" +
                "(SELECT TO_USER_ID FROM FRIENDSHIPS WHERE FROM_USER_ID=?)";
        return new ArrayList<>(template.query(sqlQuery, this::mapRowUser, id)) {
        };
    }

    public User mapRowUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    }
}
