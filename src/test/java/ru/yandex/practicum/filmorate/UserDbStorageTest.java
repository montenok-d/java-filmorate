package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = {UserDbStorage.class, UserRowMapper.class})
class UserDbStorageTest {

    private final UserDbStorage userStorage;

    @Test
    void createAndGetUser() {
        User user = newUser("mail@gmail.com", "login", "name", LocalDate.of(1999, 12, 1));
        userStorage.create(user);
        long id = user.getId();
        User userFromStorage = userStorage.findUserById(id).get();
        assertThat(userFromStorage).hasFieldOrPropertyWithValue("id", id);
        assertThat(userFromStorage).hasFieldOrPropertyWithValue("email", "mail@gmail.com");
        assertThat(userFromStorage).hasFieldOrPropertyWithValue("login", "login");
        assertThat(userFromStorage).hasFieldOrPropertyWithValue("name", "name");
        assertThat(userFromStorage).hasFieldOrProperty("birthday");
    }

    @Test
    @Sql(scripts = {"/users.sql"})
    void findAllTest() {
        List<User> users = (List<User>) userStorage.findAll();
        assertEquals(3, users.size());
        Assertions.assertThat(users).isNotEmpty().isNotNull().doesNotHaveDuplicates();
    }

    @Test
    @Sql(scripts = {"/users.sql"})
    void updateTest() {
        User user = newUser("mail@gmail.com", "login", "name", LocalDate.of(1999, 12, 1));
        userStorage.create(user);
        long id = user.getId();
        User updatedUser = User.builder()
                .id(id)
                .email("mail@gmail.com")
                .login("New login")
                .name("New name")
                .birthday(LocalDate.of(1999, 12, 1))
                .build();
        userStorage.update(updatedUser);
        assertThat(updatedUser).hasFieldOrPropertyWithValue("login", "New login");
        assertThat(updatedUser).hasFieldOrPropertyWithValue("name", "New name");
    }

    @Test
    @Sql(scripts = {"/users.sql"})
    void deleteUserTest() {
        User user = newUser("mail@gmail.com", "login", "name", LocalDate.of(1999, 12, 1));
        userStorage.create(user);
        long id = user.getId();
        int allUsersSize = userStorage.findAll().size();
        userStorage.delete(id);
        assertEquals(allUsersSize - 1, userStorage.findAll().size());
    }

    @Test
    @Sql(scripts = {"/users.sql"})
    void addFriendTest() {
        User user1 = newUser("mail@gmail.com", "login 1", "name 1", LocalDate.of(1999, 12, 1));
        User user2 = newUser("mail@gmail.com", "login 2", "name 2", LocalDate.of(1999, 12, 1));
        userStorage.create(user1);
        userStorage.create(user2);
        userStorage.addFriend(user1.getId(), user2.getId());
        List<User> user1sFriends = userStorage.findAllFriends(user1.getId());
        assertEquals(1, user1sFriends.size());
        assertThat(user1sFriends.get(0)).hasFieldOrPropertyWithValue("name", "name 2");
    }

    @Test
    @Sql(scripts = {"/users.sql"})
    public void deleteFriendTest() {
        User user1 = newUser("1mail@gmail.com", "login 1", "name 1", LocalDate.of(1999, 12, 1));
        User user2 = newUser("2mail@gmail.com", "login 2", "name 2", LocalDate.of(1999, 12, 1));
        userStorage.create(user1);
        userStorage.create(user2);
        userStorage.addFriend(user1.getId(), user2.getId());
        List<User> user1sFriends = userStorage.findAllFriends(user1.getId());
        assertEquals(1, user1sFriends.size());
        userStorage.deleteFriend(user1.getId(), user2.getId());
        List<User> user1sFriendsNew = userStorage.findAllFriends(user1.getId());
        assertEquals(0, user1sFriendsNew.size());
    }

    @Test
    @Sql(scripts = {"/users.sql"})
    void findFriendsTest() {
        User user1 = newUser("1mail@gmail.com", "login 1", "name 1", LocalDate.of(1999, 12, 1));
        User user2 = newUser("2mail@gmail.com", "login 2", "name 2", LocalDate.of(1999, 2, 1));
        User user3 = newUser("3mail@gmail.com", "login 3", "name 3", LocalDate.of(1999, 11, 1));
        User user4 = newUser("4mail@gmail.com", "login 4", "name 4", LocalDate.of(1999, 8, 1));
        userStorage.create(user1);
        userStorage.create(user2);
        userStorage.create(user3);
        userStorage.create(user4);
        userStorage.addFriend(user1.getId(), user2.getId());
        userStorage.addFriend(user3.getId(), user2.getId());
        userStorage.addFriend(user3.getId(), user4.getId());
        List<User> user1sFriends = userStorage.findAllFriends(user1.getId());
        assertEquals(1, user1sFriends.size());
        List<User> user3sFriends = userStorage.findAllFriends(user3.getId());
        assertEquals(2, user3sFriends.size());
        List<User> mutualFriends = userStorage.findMutualFriends(user1.getId(), user3.getId());
        assertEquals(1, mutualFriends.size());
        assertThat(mutualFriends.get(0)).hasFieldOrPropertyWithValue("name", "name 2");
    }

    private User newUser(
            String email,
            String login,
            String name,
            LocalDate birthday) {
        return User.builder()
                .email(email)
                .login(login)
                .name(name)
                .birthday(birthday)
                .build();
    }
}