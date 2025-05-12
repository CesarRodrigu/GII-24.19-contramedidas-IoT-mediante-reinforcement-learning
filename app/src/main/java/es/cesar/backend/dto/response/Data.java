package es.cesar.backend.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Data {
    private String content;
    private String name;
    private String type;
}