package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private int id = 0;

    @Autowired
    public UserService(UserDbStorage userStorage) {
        this.userStorage = userStorage;
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
        /*if (inMemoryUserStorage.getUserById(id).getFriends().isEmpty() || inMemoryUserStorage.getUserById(otherId).getFriends().isEmpty()) {
            return Collections.emptyList();
        }
        Set<Integer> listFriendsId = inMemoryUserStorage.getUserById(id).getFriends();
        Set<Integer> otherFriendsId = inMemoryUserStorage.getUserById(otherId).getFriends();
        List<Integer> duplicates = new ArrayList<>();
        for (Integer i : listFriendsId) {
            if (otherFriendsId.contains(i)) {
                duplicates.add(i);
            }
        }
        return inMemoryUserStorage.getUsers().stream()
                .filter(user -> duplicates.contains(user.getId()))
                .collect(Collectors.toList());*/
        return null;
    }

    public Collection<User> getListFriends(int id) {
        /*if (inMemoryUserStorage.getUserById(id).getFriends().isEmpty()) {
            return Collections.emptyList();
        }
        Set<Integer> listFriendsId = inMemoryUserStorage.getUserById(id).getFriends();
        return inMemoryUserStorage.getUsers().stream()
                .filter(user -> listFriendsId.contains(user.getId()))
                .collect(Collectors.toList());*/
        return null;
    }

    public User addFriend(int id, int friendId) {
       /* if (!inMemoryUserStorage.existById(id) || !inMemoryUserStorage.existById(friendId)) {
            throw new ObjectNotFoundException("user not found");
        }
        User user = inMemoryUserStorage.getUserById(id);
        inMemoryUserStorage.getUserById(friendId).getFriends().add(id);
        user.getFriends().add(friendId);
        return user;*/
        return null;
    }

    public User deleteFriend(int id, int friendId) {
        return userStorage.deleateFriend(id, friendId);
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

    private int generateID() {
        return ++this.id;
    }
}
