package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDate;

public class InMemoryFilmStorageTest {
    InMemoryFilmStorage manager;
    Film filmTest1;
    Film filmTest2;


    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryFilmStorage();
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
    }

    @Test
    void createNewFilmUser() throws ValidationException {
        manager.createFilm(filmTest1);
        manager.createFilm(filmTest2);
        Assertions.assertEquals(manager.getAllFilms().size(), 2, "Films not save");

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
        Assertions.assertEquals(manager.getAllFilms().size(), 1, "validation film description error");
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
        Assertions.assertEquals(manager.getAllFilms().size(), 1, "validation of releaseDate error");
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
}