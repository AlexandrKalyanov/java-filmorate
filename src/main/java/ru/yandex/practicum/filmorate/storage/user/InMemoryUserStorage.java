package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {


    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public int deleteUser(int id) {
        users.remove(id);
        return id;
    }

    @Override
    public User getUserById(int id) {
        return users.get(id);
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public boolean existById(int id) {
        return users.containsKey(id);
    }

    @Override
    public User deleateFriend(int id, int friendId) {
      /*  User user = users.get(id);
        user.getFriends().remove(friendId);
        User userFriend = users.get(friendId);
        userFriend.getFriends().remove(id);*/
        return null;
    }
}
