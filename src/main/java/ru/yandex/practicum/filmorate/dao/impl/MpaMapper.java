package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MpaMapper implements RowMapper<Mpa> {

    @Override
    public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(rs.getInt("ID_MPA"))
                .name(rs.getString("NAME_MPA"))
                .build();
    }
}
