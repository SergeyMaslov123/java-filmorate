package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
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
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(int idUser, int idFriend) throws ValidationException {
        if (userStorage.isValidUser(idUser) && userStorage.isValidUser(idFriend)) {
            User user = userStorage.getUserForId(idUser);
            Set<Integer> friendsUser;
            if (user.getFriends() == null) {
                friendsUser = new HashSet<>();
            } else {
                friendsUser = user.getFriends();
            }
            friendsUser.add(idFriend);
            user.setFriends(friendsUser);
            userStorage.updateUser(user);
            User user2 = userStorage.getUserForId(idFriend);
            Set<Integer> friendsUser2;
            if (user2.getFriends() == null) {
                friendsUser2 = new HashSet<>();
            } else {
                friendsUser2 = user2.getFriends();
            }
            friendsUser2.add(idUser);
            user2.setFriends(friendsUser2);
            userStorage.updateUser(user2);

        } else {
            throw new UserNotFoundException("user not found" + idUser + " " + idFriend);
        }
    }

    public void deleteFriend(int idUser, int idFriend) throws ValidationException {
        if (userStorage.isValidUser(idUser) && userStorage.isValidUser(idFriend)) {
            User user = userStorage.getUserForId(idUser);
            User user2 = userStorage.getUserForId(idFriend);
            if (user.getFriends() == null || user2.getFriends() == null) {
                throw new UserNotFoundException("User id " + idUser + " does not contain list of friends.");
            } else if (user.getFriends().isEmpty() || user2.getFriends().isEmpty()) {
                throw new UserNotFoundException("User id " + idUser + " does not contain list of friends.");
            } else if (!user.getFriends().contains(idFriend)) {
                throw new UserNotFoundException("User id " + idUser + " not friends with user id " + idFriend);
            } else {
                Set<Integer> newFriend1 = user.getFriends();
                Set<Integer> newFriend2 = user2.getFriends();
                newFriend1.remove(idFriend);
                newFriend2.remove(idUser);
                user.setFriends(newFriend1);
                user2.setFriends(newFriend2);
                userStorage.updateUser(user);
                userStorage.updateUser(user2);
            }
        } else {
            throw new UserNotFoundException("user not found" + idUser + " " + idFriend);
        }
    }

    public List<User> getAllFriends(int idUser) throws ValidationException {
        User user = userStorage.getUserForId(idUser);
        List<User> allFriend = new ArrayList<>();
        if (user.getFriends() != null) {
            if (!user.getFriends().isEmpty()) {
                for (Integer id : user.getFriends()) {
                    allFriend.add(userStorage.getUserForId(id));
                }
            }
        }
        return allFriend;
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

