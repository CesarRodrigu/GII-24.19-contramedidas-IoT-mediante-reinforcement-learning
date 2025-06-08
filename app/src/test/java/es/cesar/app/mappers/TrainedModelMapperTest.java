package es.cesar.app.mappers;

import es.cesar.app.dto.TrainedModelDto;
import es.cesar.app.model.TrainedModel;
import es.cesar.app.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrainedModelMapperTest {

    private TrainedModelMapper trainedModelMapper;

    @BeforeEach
    void setUp() {
        trainedModelMapper = new TrainedModelMapper();
    }

    @Test
    void testUpdateEntity() {
        final String name = "ModelName";
        final String description = "Model description";

        TrainedModelDto dto = new TrainedModelDto();
        dto.setName(name);
        dto.setDescription(description);

        TrainedModel model = new TrainedModel();
        trainedModelMapper.updateEntity(dto, model);

        assertEquals(name, model.getName(), "Name was not updated correctly.");
        assertEquals(description, model.getDescription(), "Description was not updated correctly.");
    }

    @Test
    void testUpdateEntity_nullInputs() {
        TrainedModel model = new TrainedModel();
        assertDoesNotThrow(() -> trainedModelMapper.updateEntity(null, model), "Should not throw when DTO is null.");

        TrainedModelDto dto = new TrainedModelDto();
        assertDoesNotThrow(() -> trainedModelMapper.updateEntity(dto, null), "Should not throw when TrainedModel is null.");
    }

    @Test
    void testToDto() {
        final Long userId = 10L;
        final String name = "TestModel";
        final Long modelId = 20L;
        final String description = "Test description";

        User user = new User();
        user.setId(userId);

        TrainedModel model = new TrainedModel();
        model.setUser(user);
        model.setName(name);
        model.setModelId(modelId);
        model.setDescription(description);

        TrainedModelDto dto = trainedModelMapper.toDto(model);

        assertNotNull(dto, "DTO should not be null.");
        assertEquals(userId, dto.getUserId(), "UserId mismatch.");
        assertEquals(name, dto.getName(), "Name mismatch.");
        assertEquals(modelId, dto.getModelId(), "ModelId mismatch.");
        assertEquals(description, dto.getDescription(), "Description mismatch.");
    }

    @Test
    void testToDto_nullInput() {
        assertNull(trainedModelMapper.toDto(null), "Should return null when input model is null.");
    }

    @Test
    void testToDtos() {
        final Long userId = 1L;
        final String modelName1 = "Model1";
        final String modelName2 = "Model2";

        User user = new User();
        user.setId(userId);

        TrainedModel model1 = new TrainedModel();
        model1.setName(modelName1);
        model1.setUser(user);

        TrainedModel model2 = new TrainedModel();
        model2.setName(modelName2);
        model2.setUser(user);

        List<TrainedModel> models = List.of(model1, model2);
        Collection<TrainedModelDto> dtos = trainedModelMapper.toDtos(models);

        assertEquals(2, dtos.size(), "DTOs collection size mismatch.");
    }

    @Test
    void testIsDifferent_shouldReturnFalse_whenFieldsEqual() {
        final String name = "ModelName";
        final String description = "Description";

        TrainedModel model = new TrainedModel();
        model.setName(name);
        model.setDescription(description);

        TrainedModelDto dto = new TrainedModelDto();
        dto.setName(name);
        dto.setDescription(description);

        assertFalse(trainedModelMapper.isDifferent(dto, model), "Should return false when name and description match.");
    }

    @Test
    void testIsDifferent_shouldReturnTrue_whenNameDiffers() {
        final String description = "Description";

        TrainedModel model = new TrainedModel();
        model.setName("Name1");
        model.setDescription(description);

        TrainedModelDto dto = new TrainedModelDto();
        dto.setName("Name2");
        dto.setDescription(description);

        assertTrue(trainedModelMapper.isDifferent(dto, model), "Should return true when names differ.");
    }

    @Test
    void testIsDifferent_shouldReturnTrue_whenDescriptionDiffers() {
        final String name = "Name";

        TrainedModel model = new TrainedModel();
        model.setName(name);
        model.setDescription("Desc1");

        TrainedModelDto dto = new TrainedModelDto();
        dto.setName(name);
        dto.setDescription("Desc2");

        assertTrue(trainedModelMapper.isDifferent(dto, model), "Should return true when descriptions differ.");
    }

    @Test
    void testIsDifferent_shouldReturnTrue_whenBothDiffer() {
        TrainedModel model = new TrainedModel();
        model.setName("Name1");
        model.setDescription("Desc1");

        TrainedModelDto dto = new TrainedModelDto();
        dto.setName("Name2");
        dto.setDescription("Desc2");

        assertTrue(trainedModelMapper.isDifferent(dto, model), "Should return true when both name and description differ.");
    }

    @Test
    void testIsDifferent_nullInputs() {
        TrainedModel model = new TrainedModel();
        TrainedModelDto dto = new TrainedModelDto();

        assertFalse(trainedModelMapper.isDifferent(null, model), "Should return false when DTO is null.");
        assertFalse(trainedModelMapper.isDifferent(dto, null), "Should return false when model is null.");
        assertFalse(trainedModelMapper.isDifferent(null, null), "Should return false when both inputs are null.");
    }
}