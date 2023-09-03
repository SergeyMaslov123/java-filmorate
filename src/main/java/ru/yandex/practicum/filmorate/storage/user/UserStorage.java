package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.List;

public interface UserStorage {
    User createUser(User user) throws ValidationException;

    User updateUser(User user) throws ValidationException;

    List<User> getAllUsers();

    User getUserForId(int id) throws ValidationException;

    boolean isValidUser(int id) throws ValidationException;

    void addFriend(int idUser, int idFriend);

    void deleteFriend(int idUser, int idFriend);

    List<User> getAllFriends(int idUser) throws ValidationException;
}

