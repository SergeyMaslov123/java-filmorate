package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.ValidationException;
import ru.yandex.practicum.filmorate.service.Manager;

import java.util.List;

@RestController
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    Manager manager = new Manager();

    @PostMapping(value = "/users")
    public User create(@RequestBody User user) throws ValidationException {
        log.info("User create");
        return manager.createUser(user);
    }

    @PutMapping(value = "/users")
    public User update(@RequestBody User user) throws ValidationException {
        log.info("User update");
        return manager.updateUser(user);
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return manager.getAllUsers();
    }
}
