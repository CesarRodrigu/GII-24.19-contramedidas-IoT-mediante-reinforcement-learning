package es.cesar.app.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * The class CustomErrorController that handles custom error pages.
 */
@Controller
public class CustomErrorController implements ErrorController {

    /**
     * General error string.
     *
     * @param request the request
     * @param model   the model
     *
     * @return the path to the general error view
     */
    @GetMapping("/generalError")
    public String generalError(HttpServletRequest request, ModelMap model) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
        String requestUri = (String) request.getAttribute("javax.servlet.error.request_uri");

        if (requestUri == null) {
            requestUri = "Unknown";
        }

        String exceptionMessage = getExceptionMessage(throwable, statusCode);
        String message = statusCode + "error.error.statusCode" + requestUri + "error.exceptionMessage" + exceptionMessage;

        model.addAttribute("errorMessage", message);
        return "error/general";
    }

    private String getExceptionMessage(Throwable throwable, Integer statusCode) {
        if (throwable != null && throwable.getCause() != null) {
            return throwable.getCause().getMessage();
        } else if (throwable != null) {
            return throwable.getMessage();
        }
        HttpStatus httpStatus = HttpStatus.resolve(statusCode);
        return httpStatus != null ? httpStatus.getReasonPhrase() : "Error";
    }
}