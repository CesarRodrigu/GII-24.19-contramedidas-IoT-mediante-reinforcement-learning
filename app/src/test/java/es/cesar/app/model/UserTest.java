package es.cesar.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private final String username = "johndoe";
    private final String password = "password123";
    private User user;
    private Role role;

    @BeforeEach
    void setUp() {

        role = new Role("ROLE_USER");
        user = new User(username, password);
    }

    @Test
    void testUserConstructorWithUsernameAndPassword() {
        final Instant expectedCreated = user.getCreated();

        assertEquals(username, user.getUsername(), "Username does not match expected value");
        assertEquals(password, user.getPassword(), "Password does not match expected value");
        assertNotNull(expectedCreated, "Creation date should not be null");
    }

    @Test
    void testUserConstructorWithUsernamePasswordAndRoles() {


        User userWithRoles = new User(username, password, Collections.singleton(role));

        assertEquals(username, userWithRoles.getUsername(), "Username does not match expected value");
        assertEquals(password, userWithRoles.getPassword(), "Password does not match expected value");
        assertEquals(1, userWithRoles.getRoles().size(), "Roles collection should contain exactly one role");
        assertTrue(userWithRoles.getRoles().contains(role), "Roles should contain the expected role");
    }

    @Test
    void testUserConstructorWithUsernamePasswordAndSingleRole() {


        User userWithSingleRole = new User(username, password, role);

        assertEquals(username, userWithSingleRole.getUsername(), "Username does not match expected value");
        assertEquals(password, userWithSingleRole.getPassword(), "Password does not match expected value");
        assertEquals(1, userWithSingleRole.getRoles().size(), "Roles collection should contain exactly one role");
        assertTrue(userWithSingleRole.getRoles().contains(role), "Roles should contain the expected role");
    }

    @Test
    void testSetAndGetUsername() {
        final String newUsername = "janedoe";
        user.setUsername(newUsername);

        assertEquals(newUsername, user.getUsername(), "Username was not updated correctly");
    }

    @Test
    void testSetAndGetPassword() {
        final String newPassword = "newpassword";
        user.setPassword(newPassword);

        assertEquals(newPassword, user.getPassword(), "Password was not updated correctly");
    }

    @Test
    void testSetAndGetRoles() {
        final Role newRole = new Role("ROLE_ADMIN");
        user.setRoles(Collections.singleton(newRole));

        assertEquals(1, user.getRoles().size(), "Roles collection should contain exactly one role");
        assertTrue(user.getRoles().contains(newRole), "Roles should contain the expected role");
    }

    @Test
    void testSetAndGetFirstName() {
        final String firstName = "John";
        user.setFirstName(firstName);

        assertEquals(firstName, user.getFirstName(), "First name does not match expected value");
    }

    @Test
    void testSetAndGetLastName() {
        final String lastName = "Doe";
        user.setLastName(lastName);

        assertEquals(lastName, user.getLastName(), "Last name does not match expected value");
    }

    @Test
    void testSetAndGetCreated() {
        final Instant created = Instant.now();
        user.setCreated(created);

        assertEquals(created, user.getCreated(), "Creation time does not match expected value");
    }

    @Test
    void testToString() {
        final String userToString = user.toString();
        assertTrue(userToString.contains(user.getUsername()), "ToString should contain the username");
        assertFalse(userToString.contains(user.getPassword()), "ToString should not contain the password due to @JsonIgnore");
    }
}
