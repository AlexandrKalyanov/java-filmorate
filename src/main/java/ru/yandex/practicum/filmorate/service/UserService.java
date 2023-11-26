package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.FriendShipDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final FriendShipDbStorage friendShipDbStorage;


    @Autowired
    public UserService(UserDbStorage userStorage, FriendShipDbStorage friendShipDbStorage) {
        this.userStorage = userStorage;
        this.friendShipDbStorage = friendShipDbStorage;
    }

    public Collection<User> getAll() {
        Collection<User> users = userStorage.getUsers();
        log.debug("Storage size users is {}", users.size());
        return users;
    }

    public User getById(int id) {
        if (!userStorage.existById(id)) {
            throw new ObjectNotFoundException("User not found");

        }
        return userStorage.getUserById(id);
    }

    public User create(User user) {
        check(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        log.info("Add new user {}", user);
        return userStorage.addUser(user);
    }

    public User update(User user) {
        check(user);
        if (!userStorage.existById(user.getId())) {
            throw new ObjectNotFoundException("User not found");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        log.info("Update user {}", user);
        return userStorage.updateUser(user);

    }

    public int deleteUser(int id) {
        return userStorage.deleteUser(id);
    }

    public Collection<User> getListCommonFriends(int id, int otherId) {
        checkCommonFriend(id, otherId);
        return CollectionUtils.intersection(
                        friendShipDbStorage.getFromUserIDs(id),
                        friendShipDbStorage.getFromUserIDs(otherId)).stream()
                .mapToInt(Integer::valueOf)
                .mapToObj(userStorage::getUserById)
                .collect(Collectors.toList());
    }


    public Collection<User> getListFriends(int id) {
        if (!userStorage.existById(id)) {
            throw new ObjectNotFoundException("User not found");
        }
        return friendShipDbStorage.getFromUserIDs(id).stream()
                .mapToInt(Integer::valueOf)
                .mapToObj(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public void addFriend(int id, int friendId) {
        checkFriendToAdd(id, friendId);
        boolean isMutual = friendShipDbStorage.contains(friendId, id);
        friendShipDbStorage.add(friendId, id, isMutual);
    }

    public void deleteFriend(int id, int friendId) {
        checkFriendToDelete(id, friendId);
        friendShipDbStorage.delete(friendId, id);
    }

    private void check(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.debug("Incorrect email {}", user.getEmail());
            throw new ValidateException("Incorrect email");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.debug("Incorrect login {}", user.getLogin());
            throw new ValidateException("Incorrect login");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.debug("Incorrect birthday {}", user.getBirthday());
            throw new ValidateException("Incorrect login");
        }
    }

    private void checkFriendToAdd(int userID, int friendID) {
        if (!userStorage.existById(userID)) {
            throw new ObjectNotFoundException("User not found");
        }
        if (!userStorage.existById(friendID)) {
            throw new ObjectNotFoundException("User not found");
        }
        if (userID == friendID) {
            throw new ValidateException("UNABLE TO ADD");
        }
        if (friendShipDbStorage.contains(friendID, userID)) {
            throw new RuntimeException("Пользователь уже добавлен в друзья");
        }
    }

    private void checkFriendToDelete(int userID, int friendID) {
        if (!userStorage.existById(userID)) {
            throw new ObjectNotFoundException("User not found");
        }
        if (!userStorage.existById(friendID)) {
            throw new ObjectNotFoundException("User not found");
        }
        if (userID == friendID) {
            throw new ValidateException("UNABLE TO DELETE");
        }
        if (!friendShipDbStorage.contains(friendID, userID)) {
            throw new ObjectNotFoundException("Friendship not found");
        }
    }

    private void checkCommonFriend(int id, int otherId) {
        if (!userStorage.existById(id)) {
            throw new ObjectNotFoundException("User not found");
        }
        if (!userStorage.existById(otherId)) {
            throw new ObjectNotFoundException("User not found");
        }
        if (id == otherId) {
            throw new ValidateException("userId == friendId");
        }
    }
}
