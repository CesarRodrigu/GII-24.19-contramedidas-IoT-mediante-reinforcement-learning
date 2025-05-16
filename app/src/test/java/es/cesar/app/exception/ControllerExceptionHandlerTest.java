package es.cesar.app.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ControllerExceptionHandlerTest {

    private ControllerExceptionHandler handler;
    private HttpServletRequest httpRequest;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        handler = new ControllerExceptionHandler();
        httpRequest = mock(HttpServletRequest.class);
        webRequest = mock(WebRequest.class);
    }


    @Test
    void handleNoHandlerFound_shouldReturnJsonResponse_whenAcceptsJson() {
        when(httpRequest.getHeader("Accept")).thenReturn("application/json");
        when(webRequest.getDescription(false)).thenReturn("desc");

        NoHandlerFoundException ex = mock(NoHandlerFoundException.class);
        when(ex.getMessage()).thenReturn("handler not found");

        Object response = handler.handleNoHandlerFound(ex, webRequest, httpRequest);

        assertInstanceOf(ResponseEntity.class, response, "Response should be of type ResponseEntity");

        ResponseEntity<?> entity = (ResponseEntity<?>) response;
        assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode(), "HTTP status code should be 404 NOT_FOUND");
        assertInstanceOf(ErrorMessage.class, entity.getBody(), "Response body should be of type ErrorMessage");

        ErrorMessage message = (ErrorMessage) entity.getBody();
        assertEquals(404, message.getStatusCode(), "ErrorMessage status code should be 404");
        assertEquals("handler not found", message.getMessage(), "ErrorMessage message should match exception message");
        assertEquals("desc", message.getDescription(), "ErrorMessage description should match WebRequest description");
        assertNotNull(message.getTimestamp(), "ErrorMessage timestamp should not be null");
    }

    @Test
    void handleNoHandlerFound_shouldReturnModelAndView_whenNotJson() {
        when(httpRequest.getHeader("Accept")).thenReturn("text/html");

        NoHandlerFoundException ex = mock(NoHandlerFoundException.class);
        when(ex.getMessage()).thenReturn("not found");

        Object response = handler.handleNoHandlerFound(ex, webRequest, httpRequest);

        assertInstanceOf(ModelAndView.class, response, "Response should be of type ModelAndView");

        ModelAndView mav = (ModelAndView) response;
        assertEquals("error/404", mav.getViewName(), "ModelAndView view name should be 'error/404'");
        assertEquals("not found", mav.getModel().get("errorMessage"), "ModelAndView errorMessage should match exception message");
    }

    @Test
    void handleGlobal_shouldReturnJsonResponse_whenAcceptsJson() {
        when(httpRequest.getHeader("Accept")).thenReturn("application/json");
        when(webRequest.getDescription(false)).thenReturn("desc");

        Exception ex = new Exception("Global error");

        Object response = handler.handleGlobal(ex, webRequest, httpRequest);

        assertInstanceOf(ResponseEntity.class, response, "Response should be of type ResponseEntity");

        ResponseEntity<?> entity = (ResponseEntity<?>) response;
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, entity.getStatusCode(), "HTTP status code should be 500 INTERNAL_SERVER_ERROR");

        ErrorMessage message = (ErrorMessage) entity.getBody();
        assertNotNull(message, "ErrorMessage should not be null");
        assertEquals(500, message.getStatusCode(), "ErrorMessage status code should be 500");
        assertEquals("Global error", message.getMessage(), "ErrorMessage message should match exception message");
        assertEquals("desc", message.getDescription(), "ErrorMessage description should match WebRequest description");
        assertNotNull(message.getTimestamp(), "ErrorMessage timestamp should not be null");
    }

    @Test
    void handleGlobal_shouldReturnModelAndView_whenNotJson() {
        when(httpRequest.getHeader("Accept")).thenReturn("text/html");

        Exception ex = new Exception("Error occurred");

        Object response = handler.handleGlobal(ex, webRequest, httpRequest);

        assertInstanceOf(ModelAndView.class, response, "Response should be of type ModelAndView");

        ModelAndView mav = (ModelAndView) response;
        assertEquals("error/general", mav.getViewName(), "ModelAndView view name should be 'error/general'");
        assertEquals("Error occurred", mav.getModel().get("errorMessage"), "ModelAndView errorMessage should match exception message");
    }

    @Test
    void handleNoResourceFound() {
        NoResourceFoundException ex = new NoResourceFoundException(HttpMethod.GET, "No resource found");
        Object response = handler.handleNoResourceFound(ex, webRequest, httpRequest);

        assertInstanceOf(ModelAndView.class, response, "Response should be of type ResponseEntity");
    }
}
