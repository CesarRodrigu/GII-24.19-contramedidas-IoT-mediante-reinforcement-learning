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

class NameDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidName() {
        NameDto dto = new NameDto();
        dto.setName("Name");

        Set<ConstraintViolation<NameDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "There should be no validation errors for a valid name");
    }

    @Test
    void testBlankName() {
        NameDto dto = new NameDto();
        dto.setName("   ");

        Set<ConstraintViolation<NameDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "Expected one violation for blank name");
        ConstraintViolation<NameDto> violation = violations.iterator().next();
        assertTrue(violation.getMessage().contains("model.prefix"), "Message should include prefix");
    }

    @Test
    void testTooLongName() {
        NameDto dto = new NameDto();
        dto.setName("a".repeat(26));

        Set<ConstraintViolation<NameDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "Expected one violation for name length exceeding 25 characters");
        ConstraintViolation<NameDto> violation = violations.iterator().next();
        assertTrue(violation.getMessage().contains("model.prefix"), "Message should include prefix");
    }

    @Test
    void testNullName() {
        NameDto dto = new NameDto();
        dto.setName(null);

        Set<ConstraintViolation<NameDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "Expected one violation for null name");
    }
}
