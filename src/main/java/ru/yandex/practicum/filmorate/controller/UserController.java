package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        log.debug("Incoming request to get all users");
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        log.debug("Incoming request to get user by id: {}", id);
        return userService.getById(id);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.debug("Incoming request to creat user: {}", user);
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.debug("Incoming request to update user: {}", user);
        return userService.update(user);
    }

    @DeleteMapping("/{id}")
    public int delete(@PathVariable int id) {
        log.debug("Incoming request to delete user id: {}", id);
        return userService.deleteUser(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable @Positive int id, @PathVariable @Positive int friendId) {
        log.debug("Incoming request to add friend: from user ID {}, to user ID {}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable @Positive int id, @PathVariable @Positive int friendId) {
        log.debug("Incoming request to delete friend: from user ID {}, to user ID {}", id, friendId);
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getListFriends(@PathVariable int id) {
        log.debug("Incoming request to get list friends user's ID {}", id);
        return userService.getListFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getListCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.debug("Incoming request to get list common friends user_1 ID : {}, user_2 ID : {}", id, otherId);
        return userService.getListCommonFriends(id, otherId);
    }
}
