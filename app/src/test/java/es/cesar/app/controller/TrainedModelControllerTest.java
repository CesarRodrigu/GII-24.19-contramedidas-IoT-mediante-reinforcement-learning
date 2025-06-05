package es.cesar.app.controller;

import es.cesar.app.dto.TrainedModelDto;
import es.cesar.app.mappers.TrainedModelMapper;
import es.cesar.app.model.TrainedModel;
import es.cesar.app.model.User;
import es.cesar.app.repository.TrainedModelRepository;
import es.cesar.app.repository.UserRepository;
import es.cesar.app.service.LocaleFormattingService;
import es.cesar.app.service.TrainedModelService;
import es.cesar.app.service.UserService;
import es.cesar.app.util.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static es.cesar.app.util.AlertType.SUCCESS;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("coverage")
class TrainedModelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrainedModelRepository trainedModelRepository;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private TrainedModelMapper trainedModelMapper;

    @MockitoBean
    private TrainedModelService trainedModelService;

    @MockitoBean
    private LocaleFormattingService formattingService;


    private User testUser;
    private TrainedModel testModel;

    @BeforeEach
    void setup() {
        trainedModelRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setUsername("admin");
        testUser.setPassword("password");
        userRepository.save(testUser);

        testModel = new TrainedModel();
        testModel.setUser(testUser);
        testModel.setName("testModel");
        testModel.setFile(new byte[]{0, 1, 2});
        testModel.setFileName("test.zip");
        trainedModelRepository.save(testModel);
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testManageTrainedModels() throws Exception {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUsername).thenReturn(testUser.getUsername());
            mockMvc.perform(get("/manageTrainedModels"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("trained_models/table"))
                    .andExpect(model().attributeExists("trainedModelList"))
                    .andExpect(model().attributeExists("nameDto"));
        }
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testRequestNewModel() throws Exception {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUsername).thenReturn(testUser.getUsername());

            mockMvc.perform(post("/requestModel").with(csrf())
                            .param("name", "testmodel"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/manageTrainedModels"));
        }
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testRequestNewModel_WithValidationErrors() throws Exception {
        mockMvc.perform(post("/requestModel").with(csrf())
                        .param("name", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("trained_models/table"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testRequestNewModel_AlreadyExistsRequest() throws Exception {
        final String testModelName = "testmodel";
        final String testUsername = testUser.getUsername();
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUsername).thenReturn(testUsername);

            when(userService.getUserByUsername(testUsername)).thenReturn(testUser);
            when(trainedModelService.isTrainedModelExists(testUser, testModelName)).thenReturn(true);
            when(formattingService.getMessage("model.exists")).thenReturn("Model already exists");

            mockMvc.perform(post("/requestModel").with(csrf())
                            .param("name", testModelName))
                    .andExpect(status().isOk())
                    .andExpect(view().name("trained_models/table"))
                    .andExpect(model().attributeHasFieldErrors("nameDto", "name"))
                    .andExpect(model().attributeExists("nameDto"));
        }
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testRequestNewModel_SuccessfulRequest() throws Exception {
        final String testModelName = "testmodel";
        final String testUsername = testUser.getUsername();

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUsername).thenReturn(testUsername);

            when(userService.getUserByUsername(testUsername)).thenReturn(testUser);
            when(trainedModelService.isTrainedModelExists(testUser, testModelName)).thenReturn(false);
            when(formattingService.getMessage("manageusers.request.success")).thenReturn("Request sent successfully");

            mockMvc.perform(post("/requestModel").with(csrf())
                            .param("name", testModelName))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/manageTrainedModels"));

            verify(trainedModelService).requestTrainedModelToFlask(testUser, testModelName);
        }
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testDeleteTrainedModel() throws Exception {
        mockMvc.perform(post("/deleteTrainedModel").with(csrf())
                        .param("modelId", testModel.getModelId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manageTrainedModels"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testUpdateModelEqual() throws Exception {
        mockMvc.perform(post("/updateModel").with(csrf())
                        .param("modelId", testModel.getModelId().toString())
                        .param("name", "updatedName"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manageTrainedModels"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testUpdateModel() throws Exception {
        final Long modelId = testModel.getModelId();
        final String updateSuccessMessage = "Model updated successfully";
        TrainedModelDto dto = new TrainedModelDto();
        dto.setModelId(modelId);
        dto.setName("updatedName");

        when(trainedModelService.getTrainedModelById(modelId)).thenReturn(testModel);
        when(trainedModelMapper.isDifferent(any(TrainedModelDto.class), eq(testModel))).thenReturn(true);
        doNothing().when(trainedModelMapper).updateEntity(any(TrainedModelDto.class), eq(testModel));
        when(trainedModelService.updateTrainedModel(testModel)).thenReturn(testModel);
        when(formattingService.getMessage("model.update.success")).thenReturn(updateSuccessMessage);

        mockMvc.perform(post("/updateModel").with(csrf())
                        .param("modelId", testModel.getModelId().toString())
                        .param("name", "updatedName"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("type", SUCCESS.getValue()))
                .andExpect(flash().attribute("error", updateSuccessMessage))
                .andExpect(redirectedUrl("/manageTrainedModels"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testEditModel() throws Exception {
        final long testModelId = testModel.getModelId();
        TrainedModelDto testDto = new TrainedModelDto();
        testDto.setModelId(testModel.getModelId());
        testDto.setName(testModel.getName());

        when(trainedModelService.getTrainedModelById(testModelId)).thenReturn(testModel);
        when(trainedModelMapper.toDto(testModel)).thenReturn(testDto);
        
        mockMvc.perform(get("/editModel").with(csrf())
                        .param("modelId", String.valueOf(testModelId)))
                .andExpect(status().isOk())
                .andExpect(view().name("trained_models/edit"))
                .andExpect(model().attributeExists("trainedModelDto"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testDownloadZipFile() throws Exception {
        final long testModelId = testModel.getModelId();
        when(trainedModelService.getTrainedModelById(testModelId)).thenReturn(testModel);
        mockMvc.perform(get("/models/" + testModelId + "/download"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testUploadZipModel() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test2.zip",
                "application/zip",
                new byte[]{1, 2, 3}
        );
        mockMvc.perform(multipart("/uploadZipModel")
                        .file(mockFile)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manageTrainedModels"));

    }
}
