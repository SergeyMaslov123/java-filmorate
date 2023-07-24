package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.ValidationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Manager {
    private static final Logger log = LoggerFactory.getLogger(Manager.class);
    protected static HashMap<Integer, Film> allFilms = new HashMap<>();
    HashMap<Integer, User> allUsers = new HashMap<>();
    protected int idFilms = 0;
    protected int idUsers = 0;
    public Film createFilm(Film film) throws ValidationException {
        if (validFilm(film)) {
            film.setId(generateIdFilm());
            allFilms.put(film.getId(), film);
            return film;
        } else {
            throw new ValidationException("ошибка валидации " + film.toString());
        }
    }
    public Film updateFilm(Film film) throws ValidationException {
        if(allFilms.containsKey(film.getId())) {
            if (validFilm(film)) {
                allFilms.put(film.getId(), film);
                return film;
            } else {
                throw new ValidationException("ошибка валидации filmUpdate " + film.toString());
            }
        } else {
            throw new ValidationException("error, id " + film.getId() + " not created!" );
        }
    }
    public List<Film> getAllFilms (){
        return new ArrayList<>(allFilms.values());
    }
    public User createUser (User user) throws ValidationException {
        if (validUser(user)) {
            user.setId(generateIdUser());
            validUserName(user);
            allUsers.put(user.getId(), user);
            return user;
        } else {
            throw new ValidationException("ошибка валидации User " + user.toString());
        }
    }
    public User updateUser (User user) throws ValidationException {
        if (allUsers.containsKey(user.getId())) {
            if (validUser(user)) {
                validUserName(user);
                allUsers.put(user.getId(), user);
                return user;
            } else {
                throw new ValidationException("ошибка валидации UserUpdate");
            }
        } else {
            throw new ValidationException("Error ! id user " + user.getId() + " not created" );
        }
    }
    public List<User> getAllUsers() {
        return new ArrayList<>(allUsers.values());
    }
    private int generateIdUser() {
        for (User user: allUsers.values()) {
            idUsers = user.getId();
        }
        return idUsers + 1;
    }
    private int generateIdFilm() {
        for (Film film: allFilms.values()) {
            idFilms = film.getId();
        }
        return idFilms + 1;
    }
    private boolean validFilm(Film film) {
        if (film.getName().isEmpty() || film.getDescription().length() > 200
                || film.getReleaseDate().isBefore(LocalDate.of(1895,12,28 ))
                || film.getDuration() <= 0) {
            log.warn("valid Film error " + film.toString());
            return false;
        }
        return true;
    }
    public boolean validUser(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@") || user.getLogin().isBlank()
                || user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("valid User error " + user.toString());
            return false;
        }
        return true;
    }
    public User validUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return user;
    }

}
