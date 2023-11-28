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
    public Collection<User> getFriendsByUser(int id) {
        return template.query("SELECT user_id, email, login, name, birthday FROM users WHERE user_id IN" +
                "(SELECT FROM_USER_ID FROM FRIENDSHIPS WHERE TO_USER_ID=?)", this::mapRowUser, id);
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
