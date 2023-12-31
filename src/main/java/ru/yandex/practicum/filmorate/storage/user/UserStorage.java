package ru.yandex.practicum.filmorate.storage.user;


import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    void addUser(User user);

    void deleteUser(int id);

    void updateUser(User film);

    User getUserById(int id);

    Collection<User> getUsers();

    boolean existById(int id);

    User deleateFriend(int id, int friendId);


}
