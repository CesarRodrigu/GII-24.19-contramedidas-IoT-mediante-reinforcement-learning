package es.cesar.app.dto.response;

import lombok.Data;

/**
 * The Data Class that represents an ErrorResponse.
 */
@Data
public class ErrorResponse {
    private int code;
    private String name;
    private String description;
}