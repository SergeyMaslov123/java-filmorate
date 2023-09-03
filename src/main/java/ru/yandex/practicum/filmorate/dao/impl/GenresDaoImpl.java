package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenresDao;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class GenresDaoImpl implements GenresDao {
    private final JdbcTemplate jdbcTemplate;

    public GenresDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void setGenres(int idFilm, Set<Genre> genres) {
        if (genres != null) {
            for (Genre g : genres) {
                jdbcTemplate.update("INSERT INTO film_genres(id_genre, id_film) VALUES (?,?)", g.getId(), idFilm);
            }
        }
    }

    @Override
    public Set<Genre> getGenres(int idFilm) {
        String sqlQuery = "SELECT * FROM GENRES g WHERE ID_GENRE  " +
                "IN (SELECT ID_GENRE FROM FILM_GENRES fg WHERE ID_FILM = ?)";
        return new HashSet<>(jdbcTemplate.query(sqlQuery, new Object[]{idFilm}, new GenreMapper()));
    }

    @Override
    public Set<Genre> updateGenres(int idFilm, Set<Genre> genres) {
        jdbcTemplate.update("DELETE FROM FILM_GENRES WHERE ID_FILM= ?", idFilm);
        if (genres != null) {
            for (Genre genre : genres) {
                String sqlQuery = "INSERT INTO film_genres(ID_GENRE, ID_FILM) VALUES(?,?)";
                jdbcTemplate.update(sqlQuery, genre.getId(), idFilm);
            }
        }
        return getGenres(idFilm);
    }

    @Override
    public Genre getGenreForId(int idGenre) {
        Genre genre = jdbcTemplate.query("SELECT * FROM GENRES WHERE ID_GENRE =?", new Object[]{idGenre}, new GenreMapper())
                .stream().findAny().orElse(null);
        if (genre == null) {
            throw new GenreNotFoundException("genre not found");
        } else {
            return genre;
        }
    }

    @Override
    public List<Genre> getAllGenre() {
        return jdbcTemplate.query("SELECT * FROM GENRES", new GenreMapper());
    }
}
