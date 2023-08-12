package ru.yandex.practicum.filmorate.model;


import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {
    @NonNull
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    @Builder.Default
    private Set<Integer> like = new HashSet<>();
    @Builder.Default
    private int id = 0;
}
