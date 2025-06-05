package es.cesar.app.mappers;

import es.cesar.app.dto.TrainedModelDto;
import es.cesar.app.model.TrainedModel;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * The component class TrainedModelMapper, that maps between TrainedModel and TrainedModelDto.
 */
@Component
public class TrainedModelMapper {
    /**
     * Update the entity.
     *
     * @param dto          the dto
     * @param trainedModel the trained model
     */
    public void updateEntity(TrainedModelDto dto, TrainedModel trainedModel) {
        if (dto == null || trainedModel == null) return;

        trainedModel.setName(dto.getName());
        trainedModel.setDescription(dto.getDescription());

    }

    /**
     * Transform the trained models to dtos collection.
     *
     * @param trainedModels the trainedModels
     *
     * @return the collection
     */
    public Collection<TrainedModelDto> toDtos(Collection<TrainedModel> trainedModels) {
        return trainedModels.stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Transform the trained model to dto.
     *
     * @param trainedModel the trained model
     *
     * @return the trained model dto
     */
    public TrainedModelDto toDto(TrainedModel trainedModel) {
        if (trainedModel == null) return null;

        TrainedModelDto dto = new TrainedModelDto();
        dto.setUserId(trainedModel.getUserId());
        dto.setName(trainedModel.getName());
        dto.setModelId(trainedModel.getModelId());
        dto.setDescription(trainedModel.getDescription());

        return dto;
    }

    /**
     * Compare if the dto and the trained model are different.
     *
     * @param dto          the dto
     * @param trainedModel the trained model
     *
     * @return the boolean
     */
    public boolean isDifferent(TrainedModelDto dto, TrainedModel trainedModel) {
        if (dto == null || trainedModel == null) return false;

        return !dto.getName().equals(trainedModel.getName()) ||
                !dto.getDescription().equals(trainedModel.getDescription());
    }
}
