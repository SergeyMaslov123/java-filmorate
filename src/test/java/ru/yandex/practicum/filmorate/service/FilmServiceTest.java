package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;

import static org.springframework.test.util.AssertionErrors.assertEquals;

public class FilmServiceTest {
    InMemoryFilmStorage inMemoryFilmStorage;
    FilmService filmService;
    Film filmTest1;
    Film filmTest2;

    @BeforeEach
    public void beforeEach() {
        inMemoryFilmStorage = new InMemoryFilmStorage();
        filmService = new FilmService(inMemoryFilmStorage);
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
    void likeFilm() throws ValidationException {
        inMemoryFilmStorage.createFilm(filmTest1);
        Film film = inMemoryFilmStorage.getFilmForId(1);
        assertEquals("Like ERROR", film.getLike().size(), 0);
        filmService.addLikeFilm(1, 1);
        filmService.addLikeFilm(2, 1);
        assertEquals("Like ERROR", film.getLike().size(), 2);
        filmService.deleteLike(1, 2);
        film = inMemoryFilmStorage.getFilmForId(1);
        assertEquals("delete like error", film.getLike().size(), 1);
    }

    @Test
    void topListFilm() throws ValidationException {
        inMemoryFilmStorage.createFilm(filmTest1);
        inMemoryFilmStorage.createFilm(filmTest2);
        filmService.addLikeFilm(1, 2);
        Film film = inMemoryFilmStorage.getFilmForId(2);
        assertEquals("TOP list error", filmService.listFilmTopLike("5").get(0), film);
        filmService.addLikeFilm(1, 1);
        filmService.addLikeFilm(2, 1);
        Film film1 = inMemoryFilmStorage.getFilmForId(1);
        assertEquals("TOP list error", filmService.listFilmTopLike("5").get(0), film1);
    }
}
