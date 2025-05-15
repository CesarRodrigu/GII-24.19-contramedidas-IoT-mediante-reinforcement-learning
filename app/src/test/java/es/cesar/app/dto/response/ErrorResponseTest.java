package es.cesar.app.dto.response;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorResponseTest {

    @Test
    void testErrorResponseProperties() {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        int expectedCode = httpStatus.value();
        String expectedDescription = httpStatus.getReasonPhrase();
        String expectedName = "InvalidParameter";

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(expectedCode);
        errorResponse.setDescription(expectedDescription);
        errorResponse.setName(expectedName);

        assertEquals(expectedCode, errorResponse.getCode(), "Error code does not match expected value");
        assertEquals(expectedDescription, errorResponse.getDescription(), "Error description does not match expected value");
        assertEquals(expectedName, errorResponse.getName(), "Error name does not match expected value");
    }
}
