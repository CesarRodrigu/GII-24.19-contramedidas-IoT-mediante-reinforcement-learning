package es.cesar.app.mappers;

import es.cesar.app.dto.TrainedModelDto;
import es.cesar.app.model.TrainedModel;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class TrainedModelMapper {
    public void updateEntity(TrainedModelDto dto, TrainedModel trainedModel) {
        if (dto == null || trainedModel == null) return;

        trainedModel.setName(dto.getName());
        trainedModel.setDescription(dto.getDescription());

    }

    public Collection<TrainedModelDto> toDtos(Collection<TrainedModel> users) {
        return users.stream()
                .map(this::toDto)
                .toList();
    }

    public TrainedModelDto toDto(TrainedModel trainedModel) {
        if (trainedModel == null) return null;

        TrainedModelDto dto = new TrainedModelDto();
        dto.setUserId(trainedModel.getUserId());
        dto.setName(trainedModel.getName());
        dto.setModelId(trainedModel.getModelId());
        dto.setDescription(trainedModel.getDescription());

        return dto;
    }

    public boolean isDifferent(TrainedModelDto dto, TrainedModel trainedModel) {
        if (dto == null || trainedModel == null) return false;

        return !dto.getName().equals(trainedModel.getName()) ||
                !dto.getDescription().equals(trainedModel.getDescription());
    }
}
