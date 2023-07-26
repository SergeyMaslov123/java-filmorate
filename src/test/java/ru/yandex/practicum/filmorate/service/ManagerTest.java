package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
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
                .duration(100)
                .build();
        filmTest2 = Film.builder()
                .name("testName2")
                .description("testDescription2")
                .releaseDate(LocalDate.of(2006, 3, 2))
                .duration(250)
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
        assertEquals(manager.getAllFilms().size(), 2, "Films not save");
        assertEquals(manager.getAllUsers().size(), 2, "User not save");
    }

    @Test
    void validFilmDescriptionLength() throws ValidationException {
        char[] test = new char[201];
        String testDescriptionLength201 = new String(test);
        filmTest1.setDescription(testDescriptionLength201);
        Assertions.assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                manager.createFilm(filmTest1);
            }
        });
        char[] test200 = new char[200];
        String testDescription200 = new String(test200);
        filmTest2.setDescription(testDescription200);
        manager.createFilm(filmTest2);
        assertEquals(manager.getAllFilms().size(), 1, "validation film description error");
    }

    @Test
    void validFilmName() {
        filmTest1.setName("  ");
        Assertions.assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                manager.createFilm(filmTest1);
            }
        });
    }

    @Test
    void validFilmReleaseDate() throws ValidationException {
        filmTest1.setReleaseDate(LocalDate.of(1895, 12, 27));
        Assertions.assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                manager.createFilm(filmTest1);
            }
        });
        filmTest2.setReleaseDate(LocalDate.of(1895, 12, 28));
        manager.createFilm(filmTest2);
        assertEquals(manager.getAllFilms().size(), 1, "validation of releaseDate error");
    }

    @Test
    void validFilmDuration() {
        filmTest1.setDuration(0);
        Assertions.assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                manager.createFilm(filmTest1);
            }
        });
        filmTest2.setDuration(-1);
        Assertions.assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                manager.createFilm(filmTest2);
            }
        });
    }

    @Test
    void validUserEmail() {
        userTest1.setEmail(" ");
        Assertions.assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                manager.createUser(userTest1);
            }
        });
        userTest2.setEmail("123");
        Assertions.assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                manager.createUser(userTest2);
            }
        });
    }

    @Test
    void validUserLogin() {
        userTest1.setLogin(" ");
        Assertions.assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                manager.createUser(userTest1);
            }
        });
        userTest2.setLogin("we we");
        Assertions.assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                manager.createUser(userTest2);
            }
        });
    }

    @Test
    void validUserBirthday() {
        userTest1.setBirthday(LocalDate.of(2027, 12, 23));
        Assertions.assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                manager.createUser(userTest1);
            }
        });
    }

    @Test
    void validUserName() throws ValidationException {
        userTest1.setName(" ");
        manager.createUser(userTest1);
        assertEquals(userTest1.getName(), userTest1.getLogin(), "validation name error");
    }
}
