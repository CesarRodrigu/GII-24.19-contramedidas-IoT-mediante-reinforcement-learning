package es.cesar.app.config.details;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * The type Custom user details, that is used for Spring Security.
 */
@Getter
@Setter
public class CustomUserDetails extends User {
    /**
     * Instantiates a new Custom user details.
     *
     * @param username    the username of the user
     * @param password    the password of the user
     * @param authorities the authorities of the user
     */
    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

}
