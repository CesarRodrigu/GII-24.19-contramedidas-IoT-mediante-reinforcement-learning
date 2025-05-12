package es.cesar.backend.dto;

import lombok.Data;

@Data
public class TrainedModelDto {
    private Long userId;
    private Long modelId;
    private String name;
    private String description;
}