package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component("FilmDbStorageBean")
public class FilmDbStorage implements FilmStorage {
    private static final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);
    private final JdbcTemplate jdbcTemplate;
    private final GenresDaoImpl genresDao;
    private final UserDbStorage userDbStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenresDaoImpl genresDao, UserDbStorage userDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genresDao = genresDao;
        this.userDbStorage = userDbStorage;
    }

    @Override
    public Film createFilm(Film film) throws ValidationException {
        if (isValidFilm(film)) {
            java.sql.Date sqlDate = Date.valueOf(film.getReleaseDate());
            String sqlQuery = "INSERT INTO FILMS(name, description, release_date, duration, id_mpa) " +
                    "values (?, ?, ?, ?, ?)";

            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id_film"});
                stmt.setString(1, film.getName());
                stmt.setString(2, film.getDescription());
                stmt.setDate(3, sqlDate);
                stmt.setInt(4, film.getDuration());
                stmt.setInt(5, film.getMpa().getId());
                return stmt;
            }, keyHolder);
            film.setId(keyHolder.getKey().intValue());
            genresDao.setGenres(film.getId(), film.getGenres());
            film.setGenres(genresDao.getGenres(film.getId()));
            return film;
        } else {
            throw new ValidationException("validation is fail " + film.toString());
        }
    }

    @Override
    public Film updateFilm(Film film) throws ValidationException {
        if (isValidFilm(film)) {
            getFilmForId(film.getId());
            String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, id_mpa =?" +
                    " WHERE id_film = ?";
            jdbcTemplate.update(sqlQuery,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());
            film.setGenres(genresDao.updateGenres(film.getId(), film.getGenres()));
            return film;
        } else {
            throw new ValidationException("validation is fail " + film.toString());
        }
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "SELECT f.ID_FILM, f.NAME, f.DESCRIPTION, f.RELEASE_DATE ,f.DURATION ,m.NAME_MPA, m.ID_MPA, " +
                "COUNT(l.ID_USER) AS rate FROM films AS f JOIN MPA AS m ON f.ID_MPA = m.ID_MPA " +
                "LEFT JOIN LIKES l ON f.ID_FILM  = l.ID_FILM GROUP BY f.ID_FILM";
        return jdbcTemplate.query(sqlQuery, (rs, RowNum) ->
                Film.builder()
                        .id(rs.getInt("id_film"))
                        .name(rs.getString("name"))
                        .description((rs.getString("description")))
                        .releaseDate(rs.getDate("release_date").toLocalDate())
                        .duration(rs.getInt("duration"))
                        .mpa(new Mpa(rs.getInt("id_mpa"), rs.getString("name_mpa")))
                        .rate(rs.getInt("rate"))
                        .genres(genresDao.getGenres(rs.getInt("id_film")))
                        .build());
    }

    @Override
    public Film getFilmForId(int id) throws ValidationException {
        String sqlQuery = "SELECT f.ID_FILM, f.NAME, f.DESCRIPTION, f.RELEASE_DATE ,f.DURATION ,m.ID_MPA ,m.NAME_MPA, " +
                "COUNT(l.ID_USER) AS rate FROM films AS f JOIN MPA AS m ON f.ID_MPA = m.ID_MPA " +
                "LEFT JOIN LIKES l ON f.ID_FILM  = l.ID_FILM WHERE f.ID_FILM =? GROUP BY f.ID_FILM";
        Film film = jdbcTemplate.query(sqlQuery, new Object[]{id}, (rs, RowNum) ->
                Film.builder()
                        .id(rs.getInt("id_film"))
                        .name(rs.getString("name"))
                        .description((rs.getString("description")))
                        .releaseDate(rs.getDate("release_date").toLocalDate())
                        .duration(rs.getInt("duration"))
                        .mpa(new Mpa(rs.getInt("id_mpa"), rs.getString("name_mpa")))
                        .rate(rs.getInt("rate"))
                        .genres(genresDao.getGenres(rs.getInt("id_film")))
                        .build()
        ).stream().findAny().orElse(null);
        if (film == null) {
            throw new FilmNotFoundException("Film not found");
        } else {
            return film;
        }
    }

    @Override
    public void addLike(int idUser, int idFilm) {
        jdbcTemplate.update("INSERT INTO LIKES(ID_FILM,ID_USER) VALUES(?,?)", idFilm, idUser);
    }

    @Override
    public void deleteLike(int idFilm, int idUser) throws ValidationException {
        Film film = getFilmForId(idFilm);
        User user = userDbStorage.getUserForId(idUser);
        jdbcTemplate.update("DELETE FROM LIKES WHERE ID_FILM = ? AND ID_USER = ?", idFilm, idUser);
    }

    @Override
    public List<Integer> getLike(int idFilm) {
        List<Integer> rate = new ArrayList<>();
        String sqlQuery = "SELECT ID_USER FROM LIKES WHERE ID_FILM =?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, idFilm);
        while (rowSet.next()) {
            rate.add(rowSet.getInt("ID_USER"));
        }
        return rate;
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
