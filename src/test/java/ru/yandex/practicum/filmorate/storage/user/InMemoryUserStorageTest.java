package ru.yandex.practicum.filmorate.storage.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryUserStorageTest {

    InMemoryUserStorage manager;
    User userTest1;
    User userTest2;
    User userTest3;

    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryUserStorage();
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
    void validUserEmail() {
        userTest1.setEmail(" ");
        Assertions.assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                manager.createUser(userTest1);
            }
        });
        userTest2.setEmail("123");
        Assertions.assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                manager.createUser(userTest2);
            }
        });
    }

    @Test
    void validUserLogin() {
        userTest1.setLogin(" ");
        Assertions.assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                manager.createUser(userTest1);
            }
        });
        userTest2.setLogin("we we");
        Assertions.assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                manager.createUser(userTest2);
            }
        });
    }

    @Test
    void validUserBirthday() {
        userTest1.setBirthday(LocalDate.of(2027, 12, 23));
        Assertions.assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                manager.createUser(userTest1);
            }
        });
    }

    @Test
    void validUserName() throws ValidationException {
        userTest1.setName(" ");
        manager.createUser(userTest1);
        assertEquals(userTest1.getName(), userTest1.getLogin(), "validation name error");
    }
}
