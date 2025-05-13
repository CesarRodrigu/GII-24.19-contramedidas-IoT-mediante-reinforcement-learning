package es.cesar.backend.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrainedModelTest {

    private TrainedModel trainedModel;

    @BeforeEach
    void setUp() {
        trainedModel = new TrainedModel();
    }

    @Test
    void testInitializeWithFilename() {
        final User user = new User();

        final String name = "exampleModel";
        final String extension = ".zip";
        final String fileName = name + extension;

        trainedModel.initializeWithFilename(user, fileName);

        assertEquals(user, trainedModel.getUser(), "User does not match expected value");
        assertEquals(fileName, trainedModel.getFileName(), "File name does not match expected value");
        assertEquals(name, trainedModel.getName(), "Name does not match expected value");
        assertEquals(extension, trainedModel.getExtension(), "Extension does not match expected value");
    }

    @Test
    void testInitializeWithName_WhenExtensionIsNull() {
        final User user = new User();

        final String name = "exampleModel";
        final String extension = ".zip";
        final String fileName = name + extension;

        trainedModel.initializeWithName(user, name);

        assertEquals(user, trainedModel.getUser(), "User does not match expected value");
        assertEquals(name, trainedModel.getName(), "Name does not match expected value");
        assertEquals(extension, trainedModel.getExtension(), "Default extension should be " + extension);
        assertEquals(fileName, trainedModel.getFileName(), "File name should be composed of name and extension");
    }

    @Test
    void testInitializeWithName_WhenExtensionIsNotNull() {
        final User user = new User();

        final String name = "exampleModel";
        final String extension = ".zip";
        final String fileName = name + extension;

        trainedModel.setExtension(extension);
        trainedModel.initializeWithName(user, name);

        assertEquals(user, trainedModel.getUser(), "User does not match expected value");
        assertEquals(name, trainedModel.getName(), "Name does not match expected value");
        assertEquals(extension, trainedModel.getExtension(), "Default extension should be " + extension);
        assertEquals(fileName, trainedModel.getFileName(), "File name should be composed of name and extension");
    }

    @Test
    void testSetFileName() {
        final String name = "exampleModel";
        final String extension = ".zip";
        final String fileName = name + extension;

        trainedModel.setFileName(fileName);

        assertEquals(fileName, trainedModel.getFileName(), "File name does not match expected value");
        assertEquals(name, trainedModel.getName(), "Name does not match expected value");
        assertEquals(extension, trainedModel.getExtension(), "Extension does not match expected value");
    }

    @Test
    void testSetName() {
        final String extension = ".zip";
        final String name = "test";

        trainedModel.setExtension(extension);
        trainedModel.setName(name);

        assertEquals(name, trainedModel.getName(), "Name does not match expected value");
        assertEquals(name + extension, trainedModel.getFileName(), "File name should be composed of name and extension");
    }

    @Test
    void testGetUserId() {
        final User user = new User();
        final Long expectedId = 1L;

        user.setId(expectedId);
        trainedModel.setUser(user);

        assertEquals(expectedId, trainedModel.getUserId(), "User ID does not match expected value");
    }

    @Test
    void testRemoveExtension() {
        assertEquals("document", trainedModel.removeExtension("document.pdf"));

        assertEquals("document", trainedModel.removeExtension("document"));

        assertEquals(".hiddenfile", trainedModel.removeExtension(".hiddenfile"));
    }
}
