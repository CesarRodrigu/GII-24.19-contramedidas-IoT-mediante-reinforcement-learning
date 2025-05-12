package es.cesar.backend.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrainedModelDtoTest {

    @Test
    void testTrainedModelDtoProperties() {
        Long userId = 10L;
        Long modelId = 20L;
        String name = "RL Model";
        String description = "A model for mitigate DoS and DDoS attacks";

        TrainedModelDto dto = new TrainedModelDto();
        dto.setUserId(userId);
        dto.setModelId(modelId);
        dto.setName(name);
        dto.setDescription(description);

        assertEquals(userId, dto.getUserId(), "User ID does not match expected value");
        assertEquals(modelId, dto.getModelId(), "Model ID does not match expected value");
        assertEquals(name, dto.getName(), "Name does not match expected value");
        assertEquals(description, dto.getDescription(), "Description does not match expected value");
    }
}
