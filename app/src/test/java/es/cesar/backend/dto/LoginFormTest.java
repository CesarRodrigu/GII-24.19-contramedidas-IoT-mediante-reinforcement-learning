package es.cesar.backend.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginFormTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidatorInstance() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenUsernameAndPasswordAreEmpty_thenValidationFails() {
        LoginForm loginForm = new LoginForm();
        loginForm.setUsername("");
        loginForm.setPassword("");

        Set<ConstraintViolation<LoginForm>> violations = validator.validate(loginForm);

        assertEquals(2, violations.size(), "There should be 2 validation errors for empty fields");
    }

    @Test
    void whenUsernameAndPasswordAreProvided_thenValidationSucceeds() {
        LoginForm loginForm = new LoginForm();
        loginForm.setUsername("testUser123");
        loginForm.setPassword("testPass123");

        Set<ConstraintViolation<LoginForm>> violations = validator.validate(loginForm);

        assertTrue(violations.isEmpty(), "There should be no validation errors when fields are filled");
    }
}
