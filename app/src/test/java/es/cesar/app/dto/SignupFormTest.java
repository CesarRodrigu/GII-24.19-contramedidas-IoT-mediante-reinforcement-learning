package es.cesar.app.dto;

import es.cesar.app.model.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SignupFormTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidSignupForm() {
        SignupForm form = new SignupForm();
        final String username = "test";
        final String password = "secret";
        form.setUsername(username);
        form.setPassword(password);

        Set<ConstraintViolation<SignupForm>> violations = validator.validate(form);
        assertTrue(violations.isEmpty(), "There should be no validation errors for a valid form");

        User user = form.createUser();
        assertEquals(username, user.getUsername(), "Username in created User should match input");
        assertEquals(password, user.getPassword(), "Password in created User should match input");
    }

    @Test
    void testInvalidSignupForm_blankFields() {
        SignupForm form = new SignupForm();
        form.setUsername("");
        form.setPassword("");

        Set<ConstraintViolation<SignupForm>> violations = validator.validate(form);
        assertEquals(4, violations.size(), "Expected 4 violations for blank username and password");
    }

    @Test
    void testInvalidSignupForm_shortUsernameAndPassword() {
        SignupForm form = new SignupForm();
        form.setUsername("ab");
        form.setPassword("1");

        Set<ConstraintViolation<SignupForm>> violations = validator.validate(form);
        assertEquals(2, violations.size(), "Expected violations for short username and password");
    }
}
