package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GenreMapper implements RowMapper<Genre> {
    @Override
    public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("ID_GENRE"))
                .name(rs.getString("NAME_GENRE"))
                .build();
    }
}
