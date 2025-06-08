package es.cesar.app.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * The Data Class that represents a LoginForm.
 */
@Data
public class LoginForm {

    @NotEmpty(message = "{username.required}")
    private String username;

    @NotEmpty(message = "{password.required}")
    private String password;
}