package es.cesar.app.dto.response;

import lombok.Getter;
import lombok.Setter;

/**
 * The Data Class of a Response.
 */
@Setter
@Getter
public class Response {
    private Data data;
    private boolean success;

    /**
     * Gets content.
     *
     * @return the content
     */
    public String getContent() {
        return data.getContent();
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return data.getName();
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getType() {
        return data.getType();
    }
}