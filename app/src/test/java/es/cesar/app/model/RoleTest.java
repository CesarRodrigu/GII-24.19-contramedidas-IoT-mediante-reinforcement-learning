package es.cesar.app.model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void testNoArgsConstructor() {
        Role role = new Role();

        assertNotNull(role, "Role instance should not be null");
        assertNull(role.getId(), "ID should be null by default");
        assertNull(role.getRoleName(), "Role name should be null by default");
        assertNotNull(role.getUsers(), "Users set should be initialized");
        assertTrue(role.getUsers().isEmpty(), "Users set should be empty");
    }

    @Test
    void testAllArgsConstructor() {
        final Long expectedId = 1L;
        final String expectedRoleName = "ROLE_USER";
        final Set<User> users = new HashSet<>();

        Role role = new Role(expectedId, expectedRoleName, users);

        assertEquals(expectedId, role.getId(), "ID does not match expected value");
        assertEquals(expectedRoleName, role.getRoleName(), "Role name does not match expected value");
        assertEquals(users, role.getUsers(), "Users set does not match expected value");
    }

    @Test
    void testConstructorWithRoleName() {
        final String expectedRoleName = "ROLE_ADMIN";

        Role role = new Role(expectedRoleName);

        assertEquals(expectedRoleName, role.getRoleName(), "Role name does not match expected value");
        assertNull(role.getId(), "ID should be null when not set");
        assertNotNull(role.getUsers(), "Users set should be initialized");
    }

    @Test
    void testSettersAndGetters() {
        final Long expectedId = 1L;
        final String expectedRoleName = "ROLE_ADMIN";
        final Set<User> users = new HashSet<>();

        Role role = new Role();
        role.setId(expectedId);
        role.setRoleName(expectedRoleName);
        role.setUsers(users);

        assertEquals(expectedId, role.getId(), "ID does not match expected value");
        assertEquals(expectedRoleName, role.getRoleName(), "Role name does not match expected value");
        assertEquals(users, role.getUsers(), "Users set does not match expected value");
    }
}
