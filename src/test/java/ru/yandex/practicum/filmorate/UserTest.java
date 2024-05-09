package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private User user;
    private Validator validator;
    private ValidatorFactory validatorFactory;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("mail@gmail.com")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(1999, 12, 1))
                .build();
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    void shouldCreateUser() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldNotCreateUserWithWrongEmail() {
        User user1 = User.builder()
                .email("mail")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(1999, 12, 1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user1);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void shouldCreateUserWithoutName() {
        User user1 = User.builder()
                .email("mail@gmail.com")
                .login("login")
                .birthday(LocalDate.of(1999, 12, 1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user1);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldNotCreateUserWithWrongBirthday() {
        User user1 = User.builder()
                .email("mail@gmail.com")
                .login("login")
                .birthday(LocalDate.of(2026, 12, 1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user1);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }
}

