package es.cesar.backend.dto.response;

import lombok.Data;

@Data
public class ErrorResponse {
    private int code;
    private String name;
    private String description;
}