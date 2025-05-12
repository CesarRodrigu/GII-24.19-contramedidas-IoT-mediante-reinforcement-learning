package es.cesar.backend.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Response {
    private Data data;
    private boolean success;

    public String getContent() {
        return data.getContent();
    }

    public String getName() {
        return data.getName();
    }

    public String getType() {
        return data.getType();
    }
}