package es.cesar.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainedModelDto {
    private Long userId;
    private Long modelId;
    private String name;
    private String description;

}