package ru.yandex.practicum.filmorate.storage.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.ValidationException;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryUserStorage implements UserStorage {
    protected HashMap<Integer, User> allUsers = new HashMap<>();
    protected int idUsers = 0;
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserStorage.class);

    @Override
    public User createUser(User user) throws ValidationException {
        if (isValidUserParameters(user)) {
            user.setId(generateIdUser());
            isValidUserName(user);
            allUsers.put(user.getId(), user);
            return user;
        } else {
            throw new ValidationException("ошибка валидации User " + user.toString());
        }
    }

    @Override
    public User updateUser(User user) throws ValidationException {
        if (allUsers.containsKey(user.getId())) {
            if (isValidUserParameters(user)) {
                isValidUserName(user);
                allUsers.put(user.getId(), user);
                return user;
            } else {
                throw new ValidationException("ошибка валидации UserUpdate");
            }
        } else {
            throw new UserNotFoundException("Error ! id user " + user.getId() + " not created");
        }
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(allUsers.values());
    }

    @Override
    public User getUserForId(int id) throws ValidationException {
        if (allUsers.containsKey(id)) {
            return allUsers.get(id);
        } else {
            throw new UserNotFoundException("Error ! id user " + id + " not found");
        }
    }

    private int generateIdUser() {
        for (User user : allUsers.values()) {
            idUsers = user.getId();
        }
        return idUsers + 1;
    }

    private boolean isValidUserParameters(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@") || user.getLogin().isBlank()
                || user.getBirthday().isAfter(LocalDate.now()) || user.getLogin().contains(" ")) {
            log.warn("valid User error " + user.toString());
            return false;
        }
        return true;
    }

    private User isValidUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return user;
    }

    @Override
    public boolean isValidUser(int id) {
        return allUsers.containsKey(id);
    }
}
