package ru.yandex.practicum.filmorate.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 0;
    private final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public List<User> findAll() {
        log.debug("Storage size users is {}", users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        check(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getId() == 0) {
            user.setId(generateID());
        }
        users.put(user.getId(), user);
        log.info("Add new user {}", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        check(user);
        if (!users.containsKey(user.getId())) {
            throw new ValidateException("user not found");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Update film {}", user);
        return user;
    }

    private void check(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.debug("Incorrect email {}", user.getEmail());
            throw new ValidateException("Не корректно заполнено поле email");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.debug("Incorrect login {}", user.getLogin());
            throw new ValidateException("Не корректно заполнено поле login");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.debug("Incorrect birthday {}", user.getBirthday());
            throw new ValidateException("Не корректно заполнено поле birthday");
        }
    }

    private int generateID() {
        return ++this.id;
    }
}
