package es.cesar.app.util;

import es.cesar.app.config.details.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    private SecurityUtils() {
    }

    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

            return principal.getUsername();
        }
        return "";
    }
}
