package es.cesar.app.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserDtoTest {

    @Test
    void testUserDtoProperties() {
        Long id = 1L;
        String username = "testUser";
        String firstName = "testFirstName";
        String lastName = "testLastName";
        Instant now = Instant.now();
        String created = now.toString();
        List<String> roles = Arrays.asList("ROLE_ADMIN", "ROLE_USER");

        UserDto user = new UserDto();
        user.setId(id);
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setInstantCreated(now);
        user.setCreated(created);
        user.setRoles(roles);

        assertEquals(id, user.getId(), "User ID does not match expected value");
        assertEquals(username, user.getUsername(), "Username does not match expected value");
        assertEquals(firstName, user.getFirstName(), "First name does not match expected value");
        assertEquals(lastName, user.getLastName(), "Last name does not match expected value");
        assertEquals(now, user.getInstantCreated(), "InstantCreated does not match expected value");
        assertEquals(created, user.getCreated(), "Created string does not match expected value");
        assertEquals(roles, user.getRoles(), "Roles list does not match expected value");
    }
}
