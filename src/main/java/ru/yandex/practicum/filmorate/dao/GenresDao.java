package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenresDao {
    void setGenres(int idFilm, Set<Genre> genres);

    Set<Genre> getGenres(int idFilm);

    Set<Genre> updateGenres(int idFilm, Set<Genre> genres);

    Genre getGenreForId(int idGenre);

    List<Genre> getAllGenre();
}
