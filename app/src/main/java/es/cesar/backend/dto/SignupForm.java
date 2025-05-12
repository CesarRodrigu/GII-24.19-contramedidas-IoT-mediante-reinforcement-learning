package es.cesar.backend.dto;

import es.cesar.backend.model.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class SignupForm {

    private static final String NOT_BLANK_MESSAGE = "{error.notBlank.message}";

    private static final String USERNAME_PREFIX = "{username.prefix} ";
    private static final String PASSWORD_PREFIX = "{password.prefix} ";
    private static final String LENGTH_MESSAGE = "{length.message}";


    @NotBlank(message = USERNAME_PREFIX + NOT_BLANK_MESSAGE)
    @Length(min = 3, max = 12, message = USERNAME_PREFIX + LENGTH_MESSAGE)
    private String username;

    @NotBlank(message = PASSWORD_PREFIX + NOT_BLANK_MESSAGE)
    @Length(min = 5, max = 12, message = PASSWORD_PREFIX + LENGTH_MESSAGE)
    private String password;

    public User createUser() {
        return new User(getUsername(), getPassword());
    }
}