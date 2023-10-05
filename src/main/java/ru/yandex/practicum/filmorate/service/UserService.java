package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("UserDbStorageBean") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(int idUser, int idFriend) throws ValidationException {
        if (userStorage.isValidUser(idUser) && userStorage.isValidUser(idFriend)) {
            userStorage.addFriend(idUser, idFriend);
        } else {
            throw new UserNotFoundException("user not found" + idUser + " " + idFriend);
        }
    }

    public void deleteFriend(int idUser, int idFriend) throws ValidationException {
        if (userStorage.isValidUser(idUser) && userStorage.isValidUser(idFriend)) {
            userStorage.deleteFriend(idUser, idFriend);
        } else {
            throw new UserNotFoundException("user not found" + idUser + " " + idFriend);
        }
    }

    public List<User> getAllFriends(int idUser) throws ValidationException {
        return userStorage.getAllFriends(idUser);
    }

    public List<User> getGeneralFriends(int idUser, int idOtherUser) throws ValidationException {
        User user1 = userStorage.getUserForId(idUser);
        User user2 = userStorage.getUserForId(idOtherUser);
        List<User> allGeneralFriends = new ArrayList<>();
        if (user1.getFriends() != null || user2.getFriends() != null) {
            if (!user1.getFriends().isEmpty() || !user2.getFriends().isEmpty()) {
                Set<Integer> user1Friends = new HashSet<>(user1.getFriends());
                Set<Integer> user2Friends = new HashSet<>(user2.getFriends());
                user1Friends.retainAll(user2Friends);
                for (Integer id : user1Friends) {
                    allGeneralFriends.add(userStorage.getUserForId(id));
                }
            }
        }
        return allGeneralFriends;
    }
}

