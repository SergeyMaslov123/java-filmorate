package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.GenresDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@RestController
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final FilmStorage filmStorage;
    private final FilmService filmService;
    private final GenresDao genresDao;
    private final MpaDao mpaDao;

    @Autowired
    public FilmController(@Qualifier("FilmDbStorageBean") FilmStorage filmStorage, FilmService filmService,
                          GenresDao genresDao, MpaDao mpaDao) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
        this.genresDao = genresDao;
        this.mpaDao = mpaDao;
    }


    @PostMapping(value = "/films")
    public Film create(@RequestBody Film film) throws ValidationException {
        log.info("Создается film " + film.toString());
        return filmStorage.createFilm(film);
    }

    @PutMapping(value = "/films")
    public Film update(@RequestBody Film film) throws ValidationException {
        log.info("Film обновляется " + film.toString());
        return filmStorage.updateFilm(film);
    }

    @GetMapping("/films")
    public List<Film> getAllUsers() {
        return filmStorage.getAllFilms();
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable String id, @PathVariable String userId) throws ValidationException {
        filmService.addLikeFilm(Integer.parseInt(userId.trim()), Integer.parseInt(id.trim()));
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable String id, @PathVariable String userId) throws ValidationException {
        filmService.deleteLike(Integer.parseInt(id.trim()), Integer.parseInt(userId.trim()));
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false) String count) {
        return filmService.listFilmTopLike(count);

    }

    @GetMapping("/films/likes/{id}")
    public List<Integer> getLikes(@PathVariable String id) {
        return filmStorage.getLike(Integer.parseInt(id.trim()));
    }

    @GetMapping("/films/{id}")
    public Film getFilmForId(@PathVariable String id) throws ValidationException {
        return filmStorage.getFilmForId(Integer.parseInt(id.trim()));
    }

    @GetMapping("/genres/{id}")
    public Genre getGenreFilm(@PathVariable String id) {
        return genresDao.getGenreForId(Integer.parseInt(id.trim()));
    }

    @GetMapping("/genres")
    public List<Genre> getListGenre() {
        return genresDao.getAllGenre();
    }

    @GetMapping("/mpa/{id}")
    public Mpa getMPA(@PathVariable String id) {
        return mpaDao.getMpa(Integer.parseInt(id.trim()));
    }

    @GetMapping("/mpa")
    public List<Mpa> getListMpa() {
        return mpaDao.getAllMpa();
    }

}
