package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {

        this.filmStorage = filmStorage;
    }

    public void addLikeFilm(int userId, int filmId) throws ValidationException {
        Film film = filmStorage.getFilmForId(filmId);
        Set<Integer> likes;
        if (film.getLike() == null) {
            likes = new HashSet<>();
        } else {
            likes = film.getLike();
        }
        likes.add(userId);
        film.setLike(likes);
        filmStorage.updateFilm(film);
    }

    public void deleteLike(int filmId, int userId) throws ValidationException {
        Film film = filmStorage.getFilmForId(filmId);
        if (film.getLike() == null) {
            throw new FilmNotFoundException("Film id " + filmId + " like list is empty");
        } else if (film.getLike().isEmpty()) {
            throw new FilmNotFoundException("Film id " + filmId + " like list is empty");
        } else if (!film.getLike().contains(userId)) {
            throw new FilmNotFoundException("Film id " + filmId + " not like with user id " + userId);
        } else {
            Set<Integer> likes = film.getLike();
            likes.remove(userId);
            film.setLike(likes);
            filmStorage.updateFilm(film);
        }
    }

    public List<Film> listFilmTopLike(String count) {
        if (filmStorage.getAllFilms() == null) {
            throw new FilmNotFoundException("all films don`t contain likes ");
        } else if (filmStorage.getAllFilms().isEmpty()) {
            throw new FilmNotFoundException("all films don`t contain likes ");
        }
        if (count == null) {
            return filmStorage.getAllFilms().stream()
                    .sorted((f1, f2) ->
                    {
                        if (f1.getLike() == null || f2.getLike() == null) {
                            return -1;
                        } else {
                            return f2.getLike().size() - f1.getLike().size();
                        }
                    })
                    .limit(10)
                    .collect(Collectors.toList());
        } else {
            return filmStorage.getAllFilms().stream()
                    .sorted((f1, f2) ->
                    {
                        if (f1.getLike() == null || f2.getLike() == null) {
                            return -1;
                        } else {
                            return f2.getLike().size() - f1.getLike().size();
                        }
                    })
                    .limit(Integer.parseInt(count.trim()))
                    .collect(Collectors.toList());
        }
    }


}
