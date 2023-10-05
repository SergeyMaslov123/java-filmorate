package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Component
public class MpaDaoImpl implements MpaDao {
    private final JdbcTemplate jdbcTemplate;

    public MpaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Mpa getMpa(int idMpi) {
        Mpa mpa = jdbcTemplate.query("SELECT * FROM MPA WHERE ID_MPA = ?", new Object[]{idMpi}, new MpaMapper())
                .stream().findAny().orElse(null);
        if (mpa == null) {
            throw new MpaNotFoundException("MPA not found");
        } else {
            return mpa;
        }
    }

    @Override
    public List<Mpa> getAllMpa() {
        return jdbcTemplate.query("SELECT * FROM MPA", new MpaMapper());
    }

}
