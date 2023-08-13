package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@RestController
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @PostMapping(value = "/users")
    public User create(@RequestBody User user) throws ValidationException {
        log.info("User create");
        return userStorage.createUser(user);
    }

    @PutMapping(value = "/users")
    public User update(@RequestBody User user) throws ValidationException {
        log.info("User update");
        return userStorage.updateUser(user);
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriends(@PathVariable String id, @PathVariable String friendId) throws ValidationException {
        userService.addFriend(Integer.parseInt(id.trim()), Integer.parseInt(friendId.trim()));
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable String id, @PathVariable String friendId) throws ValidationException {
        userService.deleteFriend(Integer.parseInt(id.trim()), Integer.parseInt(friendId.trim()));
    }

    @GetMapping("/users/{id}")
    public User getUserForId(@PathVariable String id) throws ValidationException {
        return userStorage.getUserForId(Integer.parseInt(id.trim()));
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getAllFriends(@PathVariable String id) throws ValidationException {
        return userService.getAllFriends(Integer.parseInt(id.trim()));
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getGeneralFriends(@PathVariable String id, @PathVariable String otherId) throws ValidationException {
        return userService.getGeneralFriends(Integer.parseInt(id.trim()), Integer.parseInt(otherId.trim()));
    }
}
