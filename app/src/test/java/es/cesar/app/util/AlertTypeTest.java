package es.cesar.app.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class AlertTypeTest {

    // @formatter:off
    final List<Tuple<String, AlertType>> alertTypes = List.of(
            new Tuple<>("primary", AlertType.PRIMARY),
            new Tuple<>("secondary", AlertType.SECONDARY),
            new Tuple<>("success", AlertType.SUCCESS),
            new Tuple<>("danger", AlertType.DANGER),
            new Tuple<>("warning", AlertType.WARNING),
            new Tuple<>("info", AlertType.INFO),
            new Tuple<>("light", AlertType.LIGHT),
            new Tuple<>("dark", AlertType.DARK)
    );
    // @formatter:on


    @Test
    void testEnumValues() {
        for (Tuple<String, AlertType> alertType : alertTypes) {
            assertEquals(alertType.getLeft(), alertType.getRight().getValue(), "String value of enum " + alertType.getRight() + " should be " + alertType.getLeft());
        }

    }

    @Test
    void testEnumSize() {
        assertEquals(alertTypes.size(), AlertType.values().length, "Enum AlertType should have " + alertTypes.size() + " values");
    }

    @Test
    void testEnumValueOf() {
        for (Tuple<String, AlertType> alertType : alertTypes) {
            final String name = alertType.getLeft().toUpperCase();
            assertEquals(alertType.getRight(), AlertType.valueOf(name), "Enum value of " + alertType.getLeft() + " should be " + name);
        }
    }

    @Test
    void testInvalidEnumValueOf() {
        assertThrows(IllegalArgumentException.class, () -> AlertType.valueOf("TEST_INVALID"));
    }
}