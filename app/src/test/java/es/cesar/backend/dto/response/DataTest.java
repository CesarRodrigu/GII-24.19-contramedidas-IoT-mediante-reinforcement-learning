package es.cesar.backend.dto.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataTest {

    @Test
    void testDataMethods() {
        final String testContent = "Test Content";
        final String testName = "Test Name";
        final String testType = "Test Type";

        Data data = new Data();
        data.setContent(testContent);
        data.setName(testName);
        data.setType(testType);

        String content = data.getContent();
        String name = data.getName();
        String type = data.getType();

        assertEquals(testContent, content, "Content should match the expected value.");
        assertEquals(testName, name, "Name should match the expected value.");
        assertEquals(testType, type, "Type should match the expected value.");
    }
}
