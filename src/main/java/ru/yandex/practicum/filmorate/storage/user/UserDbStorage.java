package ru.yandex.practicum.filmorate.storage.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.thymeleaf.exceptions.TemplateInputException;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.UserMapper;

import java.sql.Date;
import java.util.Collection;

import static java.lang.String.format;

@Repository
@Slf4j
@AllArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate template;

    @Override
    public User addUser(User user) {
        template.update("INSERT INTO USERS (email, login, name, birthday) "
                        + "VALUES (?, ?, ?, ?)",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()));
        return template.queryForObject("SELECT id, email, login, name, birthday FROM users WHERE email='" + user.getEmail() + "'", new UserMapper());
    }

    @Override
    public int deleteUser(int id) {
        int row = template.update("DELETE FROM USERS where ID = ? ", id);
        if (row == 0){
            throw  new ObjectNotFoundException("Object not found");
        }
        return id;
    }

    @Override
    public User updateUser(User user) {
        template.update("UPDATE USERS SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? WHERE id =? ",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId());
        return template.queryForObject(format("SELECT id, email, login, name, birthday FROM users WHERE ID = %d",user.getId()), new UserMapper());
    }

    @Override
    public User getUserById(int id) {
        return template.queryForObject("SELECT ID, NAME, LOGIN, BIRTHDAY, EMAIL FROM users WHERE ID = '" + id + "'", new UserMapper());
    }

    @Override
    public Collection<User> getUsers() {
       return template.query("SELECT * FROM USERS", new UserMapper());
    }

    @Override
    public boolean existById(int id) {
       /* try {
            User user = template.queryForObject("SELECT * FROM USERS WHERE ID = '"+ id +"'", new UserMapper());
            if (user == null){
                return false;
            }return true;
        }catch (TemplateInputException e){
            throw new ObjectNotFoundException("объект не найден");
        }*/

        SqlRowSet userRows = template.queryForRowSet("SELECT * FROM USERS WHERE ID = ?", id);
        if(userRows.next()) {
          /* User user = new User(
                    userRows.getInt("id"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("name"),
                    userRows.getDate("birthday").toLocalDate());*/
            return true;
        } else {
            return false;
        }

    }

    @Override
    public User deleateFriend(int id, int friendId) {
        return null;
    }
}
