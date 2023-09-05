package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenresDao;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
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
            if (!genres.isEmpty()) {
                jdbcTemplate.update(con -> {
                    PreparedStatement stmt =
                            con.prepareStatement("INSERT INTO film_genres(id_genre, id_film) VALUES (?,?)");
                    for (Genre g : genres) {
                        stmt.setInt(1, g.getId());
                        stmt.setInt(2, idFilm);
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                    return stmt;
                });
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
        setGenres(idFilm, genres);
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
