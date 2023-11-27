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

    User deleteFriend(int id, int friendId);


    Collection<User> getMutualFriends(int id, int otherId);

    User addFriend(int id, int friendId);

    Collection<User> getFriendsByUserId(int id);
}
