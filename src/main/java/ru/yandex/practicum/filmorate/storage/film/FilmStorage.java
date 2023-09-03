package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.List;

public interface FilmStorage {
    Film createFilm(Film film) throws ValidationException;

    Film updateFilm(Film film) throws ValidationException;

    List<Film> getAllFilms();

    Film getFilmForId(int id) throws ValidationException;

    List<Integer> getLike(int id);

    void addLike(int idUser, int idFilm);

    void deleteLike(int idFilm, int idUser) throws ValidationException;
}
