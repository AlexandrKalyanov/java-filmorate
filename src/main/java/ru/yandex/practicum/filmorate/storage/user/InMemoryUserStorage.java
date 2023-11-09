package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage{


    private final Map<Integer, User> users = new HashMap<>();
    @Override
    public void addUser(User user) {
        users.put(user.getId(),user);
    }
    @Override
    public void deleteUser(int id) {
        users.remove(id);
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User getUserById(int id) {
        return users.get(id);
    }

    @Override
    public void updateUser(User user) {
        users.put(user.getId(),user);
    }
    public Map<Integer,User> getUsers() {
        return users;
    }

}