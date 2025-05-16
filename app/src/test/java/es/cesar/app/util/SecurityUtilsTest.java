package es.cesar.app.util;

import es.cesar.app.config.details.CustomUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SecurityUtilsTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUsername_shouldReturnUsername_whenAuthenticated() {
        final String expectedUsername = "testuser";

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getUsername()).thenReturn(expectedUsername);

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        String username = SecurityUtils.getCurrentUsername();

        assertEquals(expectedUsername, username, "Username should match the authenticated principal's username.");
    }

    @Test
    void getCurrentUsername_shouldReturnEmptyString_whenAuthenticationIsNull() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);

        SecurityContextHolder.setContext(securityContext);

        String username = SecurityUtils.getCurrentUsername();

        assertEquals("", username, "Should return empty string when authentication is null.");
    }

    @Test
    void getCurrentUsername_shouldReturnEmptyString_whenNotAuthenticated() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        String username = SecurityUtils.getCurrentUsername();

        assertEquals("", username, "Should return empty string when authentication is not authenticated.");
    }

    @Test
    void getCurrentUsername_shouldReturnEmptyString_whenPrincipalNotCustomUserDetails() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);

        when(authentication.getPrincipal()).thenReturn("NotACustomUserDetails");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        assertThrows(ClassCastException.class, SecurityUtils::getCurrentUsername);
    }
}
