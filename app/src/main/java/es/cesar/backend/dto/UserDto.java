package es.cesar.backend.dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class UserDto {
    private Long id;

    private String username;

    private String firstName;
    private String lastName;

    private Instant instantCreated;
    private String created;

    private List<String> roles;

}
