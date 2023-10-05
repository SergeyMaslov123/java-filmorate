package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.GenresDaoImpl;
import ru.yandex.practicum.filmorate.dao.impl.MpaDaoImpl;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    private final UserDbStorage userStorage;
    private final UserService userService;
    private final FilmDbStorage filmStorage;
    private final FilmService filmService;
    private final GenresDaoImpl genresDao;
    private final MpaDaoImpl mpaDao;
    User userTest1;
    User userTest2;
    User userTest3;
    Film filmTest1;
    Film filmTest2;

    @BeforeEach
    public void beforeEach() {
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
        filmTest1 = Film.builder()
                .name("testName")
                .description("testDescription")
                .releaseDate(LocalDate.of(2002, 2, 2))
                .duration(100)
                .mpa(new Mpa(1, null))
                .build();
        filmTest2 = Film.builder()
                .name("testName2")
                .description("testDescription2")
                .releaseDate(LocalDate.of(2006, 3, 2))
                .duration(250)
                .mpa(new Mpa(2, null))
                .build();

    }

    @Test
    public void testFindUserById() throws ValidationException {
        userStorage.createUser(userTest1);
        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserForId(1));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void testGetListUser() throws ValidationException {
        userStorage.createUser(userTest1);
        userStorage.createUser(userTest2);
        List<User> allUser = userStorage.getAllUsers();
        assertThat(allUser).asList().hasSize(2);
        assertThat(allUser).asList().contains(userStorage.getUserForId(1));
        assertThat(allUser).asList().contains(userStorage.getUserForId(2));

        assertThat(Optional.of(allUser.get(0))).hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("name", "testName1 "));

        assertThat(Optional.of(allUser.get(1))).hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("name", "testName2 "));
    }

    @Test
    public void testUpdateUser() throws ValidationException {
        User testUser = userStorage.createUser(userTest1);
        userTest1.setName("Test 123");
        userStorage.updateUser(userTest1);
        Optional<User> userOptional =
                Optional.ofNullable(userStorage.getUserForId(testUser.getId()));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("name", "Test 123"));
    }

    @Test
    public void testFriend() throws ValidationException {
        userStorage.createUser(userTest1);
        userStorage.createUser(userTest2);
        userStorage.createUser(userTest3);
        userService.addFriend(1, 2);
        userService.addFriend(1, 3);
        List<User> userFriend = userService.getAllFriends(1);

        assertThat(userFriend).asList().hasSize(2);
        assertThat(userFriend).asList().contains(userStorage.getUserForId(2));
        assertThat(userFriend).asList().contains(userStorage.getUserForId(3));

        userService.deleteFriend(1, 3);
        userFriend = userService.getAllFriends(1);
        assertThat(userFriend).asList().hasSize(1);
        assertThat(userFriend).asList().contains(userStorage.getUserForId(2));


        userService.addFriend(3, 2);
        userFriend = userService.getGeneralFriends(1, 3);
        assertThat(userFriend).asList().hasSize(1);
        assertThat(userFriend).asList().contains(userStorage.getUserForId(2));
    }

    @Test
    public void testFindFilmById() throws ValidationException {
        filmStorage.createFilm(filmTest1);
        Optional<Film> filmTest = Optional.ofNullable(filmStorage.getFilmForId(1));
        assertThat(filmTest)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void testGetAllFilm() throws ValidationException {
        filmStorage.createFilm(filmTest1);
        filmStorage.createFilm(filmTest2);
        List<Film> allFilm = filmStorage.getAllFilms();
        assertThat(allFilm).asList().hasSize(2);
        assertThat(allFilm).asList().contains(filmStorage.getFilmForId(1));
        assertThat(allFilm).asList().contains(filmStorage.getFilmForId(2));

        assertThat(Optional.of(allFilm.get(0))).hasValueSatisfying(film ->
                assertThat(film).hasFieldOrPropertyWithValue("name", "testName"));

        assertThat(Optional.of(allFilm.get(1))).hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("name", "testName2"));
    }

    @Test
    public void testFilmLike() throws ValidationException {
        userStorage.createUser(userTest1);
        filmStorage.createFilm(filmTest1);
        filmService.addLikeFilm(1, 1);
        List<Integer> listLike = filmStorage.getLike(1);
        assertEquals(listLike.size(), 1, "list like error");
        assertEquals(listLike.get(0), 1, "list like error");

    }

    @Test
    public void testGenreFilm() throws ValidationException {
        filmStorage.createFilm(filmTest1);
        Set<Genre> testGenre = new HashSet<>();
        testGenre.add(genresDao.getGenreForId(1));
        genresDao.setGenres(1, testGenre);
        Film testFilm = filmStorage.getFilmForId(1);

        assertEquals(testFilm.getGenres(), testGenre, "Error genre film");
        assertThat(genresDao.getGenreForId(5))
                .hasFieldOrPropertyWithValue("id", 5)
                .hasFieldOrPropertyWithValue("name", "Документальный");
        assertThat(genresDao.getAllGenre()).asList().hasSize(6);
    }

    @Test
    public void testMpa() {
        assertThat(mpaDao.getMpa(1))
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "G");
        assertThat(mpaDao.getAllMpa()).asList().hasSize(5);
    }
}
