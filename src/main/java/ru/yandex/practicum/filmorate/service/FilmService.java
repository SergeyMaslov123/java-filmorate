package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorageBean") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void addLikeFilm(int userId, int filmId) throws ValidationException {
        filmStorage.addLike(userId, filmId);
    }

    public void deleteLike(int filmId, int userId) throws ValidationException {
        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> listFilmTopLike(String count) {
        if (filmStorage.getAllFilms() == null) {
            throw new FilmNotFoundException("all films don`t contain likes ");
        } else if (filmStorage.getAllFilms().isEmpty()) {
            throw new FilmNotFoundException("all films don`t contain likes ");
        }
        if (count == null) {
            return filmStorage.getAllFilms().stream()
                    .sorted((f1, f2) -> f2.getRate() - f1.getRate())
                    .limit(10)
                    .collect(Collectors.toList());
        } else {
            return filmStorage.getAllFilms().stream()
                    .sorted((f1, f2) -> f2.getRate() - f1.getRate())
                    .limit(Integer.parseInt(count.trim()))
                    .collect(Collectors.toList());
        }
    }
}
