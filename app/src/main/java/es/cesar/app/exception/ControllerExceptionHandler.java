package es.cesar.app.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Date;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public Object handleNoResourceFound(NoResourceFoundException ex, WebRequest request, HttpServletRequest httpRequest) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return getResponse(ex, request, httpRequest, status);
    }

    private Object getResponse(Exception ex, WebRequest request, HttpServletRequest httpRequest, HttpStatus status) {
        if (wantsJson(httpRequest)) {
            ErrorMessage message = getErrorMessage(status, ex, request);
            return new ResponseEntity<>(message, status);
        } else {
            ModelAndView mav = new ModelAndView(getViewNameByStatus(status));
            mav.addObject("errorMessage", ex.getMessage());
            return mav;
        }
    }

    private boolean wantsJson(@NotNull HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        return accept != null && accept.contains("application/json");
    }

    private ErrorMessage getErrorMessage(@NotNull HttpStatus status, @NotNull Exception exception, @NotNull WebRequest request) {
        return new ErrorMessage(status.value(), new Date(), exception.getMessage(), request.getDescription(false));
    }

    private String getViewNameByStatus(HttpStatus status) {
        return switch (status) {
            case NOT_FOUND -> "error/404";
            default -> "error/general";
        };
    }

    @ExceptionHandler({NoHandlerFoundException.class, HttpRequestMethodNotSupportedException.class})
    public Object handleNoHandlerFound(NoHandlerFoundException ex, WebRequest request, HttpServletRequest httpRequest) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return getResponse(ex, request, httpRequest, status);
    }

    @ExceptionHandler(Exception.class)
    public Object handleGlobal(Exception ex, WebRequest request, HttpServletRequest httpRequest) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return getResponse(ex, request, httpRequest, status);
    }
}
