package es.cesar.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * The Data Class that represents a NameDto.
 */
@Data
public class NameDto {
    private static final String NAME_PREFIX = "{model.prefix} ";
    private static final String NOT_BLANK_MESSAGE = "{error.notBlank.message}";
    private static final String LENGTH_MESSAGE = "{length.message}";

    @NotBlank(message = NAME_PREFIX + NOT_BLANK_MESSAGE)
    @Size(min = 1, max = 25, message = NAME_PREFIX + LENGTH_MESSAGE)
    private String name;
}
