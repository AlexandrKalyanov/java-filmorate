package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final InMemoryUserStorage inMemoryUserStorage;
    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private int id = 0;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public Collection<User> getAll() {
        Collection<User> users = inMemoryUserStorage.getAllUsers();
        log.debug("Storage size users is {}", users.size());
        return users;
    }

    public User getById(int id) {
        if (!inMemoryUserStorage.getUsers().containsKey(id)) {
            throw new ObjectNotFoundException("User not found");

        }
        return inMemoryUserStorage.getUserById(id);
    }

    public User create(User user) {
        check(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getId() == 0) {
            user.setId(generateID());
        }
        inMemoryUserStorage.addUser(user);
        log.info("Add new user {}", user);
        return user;
    }

    public User update(User user) {
        check(user);
        if (!inMemoryUserStorage.getUsers().containsKey(user.getId())) {
            throw new ObjectNotFoundException("User not found");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        inMemoryUserStorage.updateUser(user);
        log.info("Update film {}", user);
        return user;
    }

    public Optional<Collection<User>> getListCommonFriends(int id, int otherId) {
        if (inMemoryUserStorage.getUsers().get(id).getFriends().isEmpty() || inMemoryUserStorage.getUsers().get(otherId).getFriends().isEmpty()) {
            return Optional.of(new ArrayList<>());
        }
        Set<Integer> listFriendsId = inMemoryUserStorage.getUsers().get(id).getFriends();
        Set<Integer> otherFriendsId = inMemoryUserStorage.getUsers().get(otherId).getFriends();
        List<Integer> duplicates = new ArrayList<>();
        for (Integer i : listFriendsId) {
            if (otherFriendsId.contains(i)) {
                duplicates.add(i);
            }
        }
        return Optional.of(inMemoryUserStorage.getUsers().values().stream()
                .filter(user -> duplicates.contains(user.getId()))
                .collect(Collectors.toList()));
    }

    public Optional<Collection<User>> getListFriends(int id) {
        if (inMemoryUserStorage.getUserById(id).getFriends().isEmpty()) {
            return Optional.of(new ArrayList<>());
        }
        Set<Integer> listFriendsId = inMemoryUserStorage.getUserById(id).getFriends();
        return Optional.of(inMemoryUserStorage.getAllUsers().stream()
                .filter(user -> listFriendsId.contains(user.getId()))
                .collect(Collectors.toList()));
    }

    public User addFriend(int id, int friendId) {
        if (!inMemoryUserStorage.getUsers().containsKey(id) || !inMemoryUserStorage.getUsers().containsKey(friendId)) {
            throw new ObjectNotFoundException("user not found");
        }
        User user = inMemoryUserStorage.getUserById(id);
        inMemoryUserStorage.getUserById(friendId).getFriends().add(id);
        user.getFriends().add(friendId);
        return user;
    }

    public User deleteFriend(int id, int friendId) {
        User user = inMemoryUserStorage.getUsers().get(id);
        user.getFriends().remove(friendId);
        return user;
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
