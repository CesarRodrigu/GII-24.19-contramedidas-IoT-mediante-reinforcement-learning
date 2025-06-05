package es.cesar.app.util;

import es.cesar.app.config.details.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * The class SecurityUtils, that provides utility methods for security-related operations.
 */
public class SecurityUtils {
    private SecurityUtils() {
    }

    /**
     * Gets current username.
     *
     * @return the current username
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

            return principal.getUsername();
        }
        return "";
    }
}
