package es.cesar.app.controller;

import es.cesar.app.model.TrainedModel;
import es.cesar.app.model.User;
import es.cesar.app.repository.TrainedModelRepository;
import es.cesar.app.repository.UserRepository;
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
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mockStatic;
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
    void testDeleteTrainedModel() throws Exception {
        mockMvc.perform(post("/deleteTrainedModel").with(csrf())
                        .param("modelId", testModel.getModelId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manageTrainedModels"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testEditModel() throws Exception {
        mockMvc.perform(get("/editModel")
                        .param("modelId", testModel.getModelId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("trained_models/edit"))
                .andExpect(model().attributeExists("trainedModelDto"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testUpdateModel() throws Exception {
        mockMvc.perform(post("/updateModel").with(csrf())
                        .param("modelId", testModel.getModelId().toString())
                        .param("name", "updatedName"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manageTrainedModels"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testDownloadZipFile() throws Exception {
        mockMvc.perform(get("/models/" + testModel.getModelId().toString() + "/download"))
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
