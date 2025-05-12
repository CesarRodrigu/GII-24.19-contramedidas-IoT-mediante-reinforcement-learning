package es.cesar.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginForm {

    @NotEmpty(message = "{username.required}")
    private String username;

    @NotEmpty(message = "{password.required}")
    private String password;
}