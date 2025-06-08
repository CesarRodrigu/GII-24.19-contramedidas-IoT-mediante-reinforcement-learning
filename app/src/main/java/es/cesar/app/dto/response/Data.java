package es.cesar.app.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Data class, that represents the response data.
 */
@Setter
@Getter
@NoArgsConstructor
public class Data {
    private String content;
    private String name;
    private String type;
}