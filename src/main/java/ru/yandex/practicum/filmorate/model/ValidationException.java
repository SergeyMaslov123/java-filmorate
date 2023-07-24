package ru.yandex.practicum.filmorate.model;

public class ValidationException extends Exception {
    public ValidationException (String massage) {
        super(massage);
    }
}
