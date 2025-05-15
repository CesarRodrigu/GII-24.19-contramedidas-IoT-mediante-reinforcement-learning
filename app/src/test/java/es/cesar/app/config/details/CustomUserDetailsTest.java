package es.cesar.app.config.details;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomUserDetailsTest {
    @Test
    void testToStringIncludesUsernameAndAuthorities() {
        final String username = "user1";
        final String password = "password123";
        final String role = "ROLE_USER";

        final SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);

        CustomUserDetails userDetails = new CustomUserDetails(username, password, Collections.singleton(authority));

        String toStringOutput = userDetails.toString();

        assertTrue(toStringOutput.contains(username), "Username should be included in toString()");
        assertTrue(toStringOutput.contains(role), "Authorities should be included in toString()");
    }
}