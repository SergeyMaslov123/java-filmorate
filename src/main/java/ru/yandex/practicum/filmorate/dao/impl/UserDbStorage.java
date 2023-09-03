package ru.yandex.practicum.filmorate.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component("UserDbStorageBean")
public class UserDbStorage implements UserStorage {
    private static final Logger log = LoggerFactory.getLogger(UserDbStorage.class);
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public User createUser(User user) throws ValidationException {
        if (isValidUserParameters(user)) {
            isValidUserName(user);
            java.sql.Date sqlDate = Date.valueOf(user.getBirthday());
            String sqlQuery = "insert into users(login, email, name, birthday) " +
                    "values (?, ?, ?, ?)";

            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id_user"});
                stmt.setString(1, user.getLogin());
                stmt.setString(2, user.getEmail());
                stmt.setString(3, user.getName());
                stmt.setDate(4, sqlDate);
                return stmt;
            }, keyHolder);
            user.setId(keyHolder.getKey().intValue());
            return user;
        } else {
            throw new ValidationException("ошибка валидации User " + user.toString());
        }
    }

    @Override
    public User updateUser(User user) throws ValidationException {
        if (isValidUserParameters(user)) {
            getUserForId(user.getId());
            isValidUserName(user);
            String sqlQuery = "UPDATE users SET name = ?, login = ?, email = ?, birthday = ? WHERE id_user = ?";
            jdbcTemplate.update(sqlQuery,
                    user.getName(),
                    user.getLogin(),
                    user.getEmail(),
                    user.getBirthday(),
                    user.getId());
            if (user.getFriends() != null) {
                deleteAllFriendsForUserId(user.getId());
                for (int idFriend : user.getFriends()) {
                    addFriend(user.getId(), idFriend);
                }
            }
            return user;
        } else {
            throw new ValidationException("ошибка валидации User " + user.toString());
        }
    }

    @Override
    public List<User> getAllUsers() {
        return jdbcTemplate.query("SELECT * FROM users", new UserMapper());
    }

    @Override
    public User getUserForId(int id) throws ValidationException {
        Set<Integer> idFriend = new HashSet<>();
        String sqlQuery = "SELECT * FROM users WHERE id_user=?";
        User user = jdbcTemplate.query(sqlQuery, new Object[]{id}, new UserMapper())
                .stream().findAny().orElse(null);
        if (user == null) {
            throw new UserNotFoundException("User id " + id + " not found");
        } else {
            for (User u : getAllFriends(id)) {
                idFriend.add(u.getId());
            }
            user.setFriends(idFriend);
            return user;
        }

    }

    @Override
    public boolean isValidUser(int id) throws ValidationException {
        if (getUserForId(id) != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void addFriend(int idUser, int idFriend) {
        String sqlQuery = "INSERT INTO FRIENDS(ID_USER,ID_FRIEND) VALUES (?,?)";
        jdbcTemplate.update(sqlQuery, idUser, idFriend);
    }

    @Override
    public void deleteFriend(int idUser, int idFriend) {
        String sqlQuery = "DELETE FROM FRIENDS WHERE ID_USER = ? AND ID_FRIEND = ?";
        jdbcTemplate.update(sqlQuery, idUser, idFriend);

    }

    @Override
    public List<User> getAllFriends(int idUser) throws ValidationException {
        String sqlQuery = "SELECT * FROM USERS WHERE ID_USER IN (SELECT ID_FRIEND FROM FRIENDS WHERE ID_USER = ?)";
        return new ArrayList<>(jdbcTemplate.query(sqlQuery, new Object[]{idUser}, new UserMapper()));
    }

    public void deleteAllFriendsForUserId(int userId) {
        jdbcTemplate.update("DELETE FROM FRIENDS WHERE ID_USER = ?", userId);
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
}
