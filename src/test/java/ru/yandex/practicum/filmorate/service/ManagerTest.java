package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.ValidationException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ManagerTest {
    Manager manager;
    Film filmTest1;
    Film filmTest2;
    User userTest1;
    User userTest2;

    @BeforeEach
    public void beforeEach() {
        manager = new Manager();
        filmTest1 = Film.builder()
                .name("testName")
                .description("testDescription")
                .releaseDate(LocalDate.of(2002, 2, 2))
                .duration(120)
                .build();
        filmTest2 = Film.builder()
                .name("testName2")
                .description("testDescription2")
                .releaseDate(LocalDate.of(2006, 3, 2))
                .duration(150)
                .build();
        userTest1 = User.builder()
                .email("12@13")
                .login("testLogin1")
                .name("testName1 ")
                .birthday(LocalDate.of(1987, 10, 25))
                .build();
        userTest2 = User.builder()
                .email("12@13")
                .login("testLogin1")
                .name("testName1 ")
                .birthday(LocalDate.of(1987, 10, 25))
                .build();
    }

    @Test
    void createNewFilmUser() throws ValidationException {
        manager.createUser(userTest1);
        manager.createUser(userTest2);
        manager.createFilm(filmTest1);
        manager.createFilm(filmTest2);
        assertEquals(manager.getAllFilms().size(), 2, "Films не сохранены");
        assertEquals(manager.getAllUsers().size(), 2, "User not save");
    }
}
