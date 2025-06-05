package es.cesar.app.dto;

import lombok.Data;

/**
 * The Data Class that represents a TrainedModelDto.
 */
@Data
public class TrainedModelDto {
    private Long userId;
    private Long modelId;
    private String name;
    private String description;
}