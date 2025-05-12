package es.cesar.backend.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorResponse {
    private int code;
    private String description;
    private String name;

}