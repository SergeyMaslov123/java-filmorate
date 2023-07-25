package ru.yandex.practicum.filmorate.model;


import lombok.*;

import java.time.Duration;
import java.time.LocalDate;

@Data
@Builder
public class Film {
    @NonNull
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    @Builder.Default
    private int id = 0;
}
