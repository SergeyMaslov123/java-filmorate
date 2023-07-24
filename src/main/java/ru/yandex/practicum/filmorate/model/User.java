package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
@Builder
public class User {
    @NonNull
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    @Builder.Default
    private int id = 0;
}
