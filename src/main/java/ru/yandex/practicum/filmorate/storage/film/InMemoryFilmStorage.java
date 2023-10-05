package ru.yandex.practicum.filmorate.storage.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final Logger log = LoggerFactory.getLogger(InMemoryFilmStorage.class);
    protected HashMap<Integer, Film> allFilms = new HashMap<>();
    protected int idFilms = 0;

    @Override
    public Film createFilm(Film film) throws ValidationException {
        if (isValidFilm(film)) {
            film.setId(generateIdFilm());
            allFilms.put(film.getId(), film);
            return film;
        } else {
            throw new ValidationException("ошибка валидации " + film.toString());
        }
    }

    @Override
    public Film updateFilm(Film film) throws ValidationException {
        if (allFilms.containsKey(film.getId())) {
            if (isValidFilm(film)) {
                allFilms.put(film.getId(), film);
                return film;
            } else {
                throw new ValidationException("ошибка валидации filmUpdate " + film.toString());
            }
        } else {
            throw new FilmNotFoundException("error, id " + film.getId() + " not created!");
        }
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(allFilms.values());
    }

    @Override
    public Film getFilmForId(int id) throws ValidationException {
        if (allFilms.containsKey(id)) {
            return allFilms.get(id);
        } else {
            throw new FilmNotFoundException("Film id " + id + "not found");
        }
    }

    @Override
    public List<Integer> getLike(int id) {
        return null;
    }

    @Override
    public void addLike(int idUser, int idFilm) {

    }

    @Override
    public void deleteLike(int idFilm, int idUser) {

    }

    private int generateIdFilm() {
        for (Film film : allFilms.values()) {
            idFilms = film.getId();
        }
        return idFilms + 1;
    }

    private boolean isValidFilm(Film film) {
        if (film.getName().isBlank() || film.getDescription().length() > 200
                || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))
                || film.getDuration() <= 0) {
            log.warn("valid Film error " + film.toString());
            return false;
        }
        return true;
    }
}
