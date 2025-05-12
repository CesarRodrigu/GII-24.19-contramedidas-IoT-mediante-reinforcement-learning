package es.cesar.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginForm {

    @NotEmpty(message = "{username.required}")
    private String username;

    @NotEmpty(message = "{password.required}")
    private String password;
}