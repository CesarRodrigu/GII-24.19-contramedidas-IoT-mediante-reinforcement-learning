package es.cesar.app.dto.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResponseTest {

    @Test
    void testResponseMethods() {
        final String testContent = "Sample Content";
        final String testName = "Sample Name";
        final String testType = "Sample Type";

        Data data = new Data();
        data.setContent(testContent);
        data.setName(testName);
        data.setType(testType);

        Response response = new Response();
        response.setData(data);
        response.setSuccess(true);

        String content = response.getContent();
        String name = response.getName();
        String type = response.getType();
        boolean success = response.isSuccess();

        assertEquals(testContent, content, "Content should match the expected value.");
        assertEquals(testName, name, "Name should match the expected value.");
        assertEquals(testType, type, "Type should match the expected value.");
        assertTrue(success, "Success flag should be true.");
    }
}
