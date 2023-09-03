package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
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
        List<Film> allFilms = new ArrayList<>();
        List<Film> films = jdbcTemplate.query("SELECT * FROM films AS f LEFT JOIN MPA AS m ON f.ID_MPA = m.ID_MPA",
                new FilmMapper());
        for (Film film : films) {
            genresDao.setGenres(film.getId(), film.getGenres());
            film.setGenres(genresDao.getGenres(film.getId()));
            film.setRate(getLike(film.getId()).size());
            allFilms.add(film);
        }
        return allFilms;
    }

    @Override
    public Film getFilmForId(int id) throws ValidationException {
        String sqlQuery = "SELECT f.id_film, " +
                "f.name, f.description, f.release_date, f.DURATION, m.id_mpa, m.name_mpa " +
                "FROM films AS f LEFT JOIN MPA AS m ON f.ID_MPA = m.ID_MPA WHERE f.ID_FILM = ?";
        Film film = jdbcTemplate.query(sqlQuery, new Object[]{id}, new FilmMapper())
                .stream().findAny().orElse(null);
        if (film != null) {
            genresDao.setGenres(film.getId(), film.getGenres());
            film.setGenres(genresDao.getGenres(film.getId()));
            film.setRate(getLike(id).size());
            return film;
        } else {
            throw new FilmNotFoundException("validation is fail + film.toString()");
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
