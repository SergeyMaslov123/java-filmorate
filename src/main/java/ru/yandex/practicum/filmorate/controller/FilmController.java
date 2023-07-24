package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.ValidationException;

import ru.yandex.practicum.filmorate.service.Manager;

import java.util.List;

@RestController
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    Manager manager = new Manager();
    @PostMapping(value = "/films")
    public Film create(@RequestBody Film film) throws ValidationException {
        log.info("Создается film " + film.toString());
        return manager.createFilm(film);
    }
    @PutMapping(value ="/films")
    public Film update(@RequestBody Film film) throws ValidationException {
        log.info("Film обновляется " + film.toString());
        return manager.updateFilm(film);
    }
    @GetMapping("/films")
    public List<Film> getAllUsers() {
        return manager.getAllFilms();
    }
}
