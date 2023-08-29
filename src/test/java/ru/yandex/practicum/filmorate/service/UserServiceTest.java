package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

public class UserServiceTest {
    InMemoryUserStorage inMemoryUserStorage;
    UserService userService;
    User userTest1;
    User userTest2;
    User userTest3;

    @BeforeEach
    public void beforeEach() {
        inMemoryUserStorage = new InMemoryUserStorage();
        userService = new UserService(inMemoryUserStorage);
        userTest1 = User.builder()
                .email("12@13")
                .login("testLogin1")
                .name("testName1 ")
                .birthday(LocalDate.of(1987, 10, 25))
                .build();
        userTest2 = User.builder()
                .email("2222@2222")
                .login("testLogin2")
                .name("testName2 ")
                .birthday(LocalDate.of(1987, 10, 25))
                .build();
        userTest3 = User.builder()
                .email("2222@2222")
                .login("testLogin2")
                .name("testName2 ")
                .birthday(LocalDate.of(1987, 10, 25))
                .build();
    }

    @Test
    void addDeleteFriend() throws ValidationException {
        inMemoryUserStorage.createUser(userTest1);
        inMemoryUserStorage.createUser(userTest2);
        userService.addFriend(1, 2);
        Assertions.assertEquals(inMemoryUserStorage.getUserForId(1).getFriends().size(), 1, "addFriends Error");
        userService.deleteFriend(1, 2);
        Assertions.assertEquals(inMemoryUserStorage.getUserForId(1).getFriends().size(), 0, "delete Error");
        Assertions.assertThrows(UserNotFoundException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                userService.addFriend(1, 5);
            }
        });
        Assertions.assertThrows(UserNotFoundException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                userService.addFriend(1, 7);
            }
        });
    }

    @Test
    void generalFriends() throws ValidationException {
        inMemoryUserStorage.createUser(userTest1);
        inMemoryUserStorage.createUser(userTest2);
        inMemoryUserStorage.createUser(userTest3);
        userService.addFriend(1, 2);
        userService.addFriend(3, 1);
        Assertions.assertEquals(userService.getGeneralFriends(2, 3).size(), 1,
                "Error general friends");

    }
}
