package es.cesar.app.dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;

/**
 * The Data Class that represents a UserDto.
 */
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
