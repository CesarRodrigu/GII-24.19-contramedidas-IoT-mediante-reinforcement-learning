package es.cesar.app.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ModelMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomErrorControllerTest {

    private CustomErrorController controller;
    private HttpServletRequest request;
    private ModelMap model;

    @BeforeEach
    void setUp() {
        controller = new CustomErrorController();
        request = mock(HttpServletRequest.class);
        model = new ModelMap();
    }

    @Test
    void generalError_shouldReturnErrorViewAndAddMessage_whenThrowableWithCause() {
        final Integer statusCode = 500;
        final String requestUri = "/test-uri";
        Throwable cause = new RuntimeException("Cause message");
        Throwable throwable = new Exception("Exception message", cause);

        when(request.getAttribute("javax.servlet.error.status_code")).thenReturn(statusCode);
        when(request.getAttribute("javax.servlet.error.exception")).thenReturn(throwable);
        when(request.getAttribute("javax.servlet.error.request_uri")).thenReturn(requestUri);

        String view = controller.generalError(request, model);

        String expectedMessage = statusCode + "error.error.statusCode" + requestUri + "error.exceptionMessage" + cause.getMessage();
        assertEquals("error/general", view, "View name should be 'error/general'.");
        assertEquals(expectedMessage, model.get("errorMessage"), "Error message not set correctly.");
    }

    @Test
    void generalError_shouldUseThrowableMessage_whenThrowableWithoutCause() {
        final Integer statusCode = 400;
        final String requestUri = "/bad-request";
        Throwable throwable = new IllegalArgumentException("Illegal argument");

        when(request.getAttribute("javax.servlet.error.status_code")).thenReturn(statusCode);
        when(request.getAttribute("javax.servlet.error.exception")).thenReturn(throwable);
        when(request.getAttribute("javax.servlet.error.request_uri")).thenReturn(requestUri);

        String view = controller.generalError(request, model);

        String expectedMessage = statusCode + "error.error.statusCode" + requestUri + "error.exceptionMessage" + throwable.getMessage();
        assertEquals("error/general", view);
        assertEquals(expectedMessage, model.get("errorMessage"));
    }

    @Test
    void generalError_shouldUseHttpStatusReasonPhrase_whenNoThrowable() {
        final Integer statusCode = 404;
        final String requestUri = "/not-found";

        when(request.getAttribute("javax.servlet.error.status_code")).thenReturn(statusCode);
        when(request.getAttribute("javax.servlet.error.exception")).thenReturn(null);
        when(request.getAttribute("javax.servlet.error.request_uri")).thenReturn(requestUri);

        String view = controller.generalError(request, model);

        String expectedMessage = statusCode + "error.error.statusCode" + requestUri + "error.exceptionMessage" + "Not Found";
        assertEquals("error/general", view);
        assertEquals(expectedMessage, model.get("errorMessage"));
    }

    @Test
    void generalError_shouldHandleNullRequestUri() {
        final Integer statusCode = 500;
        final Throwable throwable = new RuntimeException("Error occurred");

        when(request.getAttribute("javax.servlet.error.status_code")).thenReturn(statusCode);
        when(request.getAttribute("javax.servlet.error.exception")).thenReturn(throwable);
        when(request.getAttribute("javax.servlet.error.request_uri")).thenReturn(null);

        String view = controller.generalError(request, model);

        String expectedMessage = statusCode + "error.error.statusCode" + "Unknown" + "error.exceptionMessage" + throwable.getMessage();
        assertEquals("error/general", view);
        assertEquals(expectedMessage, model.get("errorMessage"));
    }

    @Test
    void generalError_shouldHandleUnknownStatusCodeGracefully() {
        final Integer statusCode = 999;
        final String requestUri = "/unknown-status";

        when(request.getAttribute("javax.servlet.error.status_code")).thenReturn(statusCode);
        when(request.getAttribute("javax.servlet.error.exception")).thenReturn(null);
        when(request.getAttribute("javax.servlet.error.request_uri")).thenReturn(requestUri);

        String view = controller.generalError(request, model);

        String expectedMessage = statusCode + "error.error.statusCode" + requestUri + "error.exceptionMessage" + "Error";
        assertEquals("error/general", view);
        assertEquals(expectedMessage, model.get("errorMessage"));
    }
}
