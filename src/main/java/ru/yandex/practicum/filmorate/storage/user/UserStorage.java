package ru.yandex.practicum.filmorate.storage.user;


import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    User addUser(User user);

    int deleteUser(int id);

    User updateUser(User film);

    User getUserById(int id);

    Collection<User> getUsers();

    boolean contains(int id);

    Collection<User> getFriendsByUser(int id);

}
