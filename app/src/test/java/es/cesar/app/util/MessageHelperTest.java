package es.cesar.app.util;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class MessageHelperTest {
    @Test
    void testAddFlashMessage() {
        RedirectAttributes ra = Mockito.mock(RedirectAttributes.class);

        final String message = "Test Flash Message";
        final AlertType alertType = AlertType.SUCCESS;
        MessageHelper.addFlashMessage(ra, alertType, message);

        verify(ra, times(1)).addFlashAttribute("type", alertType.getValue());
        verify(ra, times(1)).addFlashAttribute("error", message);
    }

    @Test
    void testAddMessage() {
        ModelMap model = Mockito.mock(ModelMap.class);

        final String message = "Test Message";
        final AlertType alertType = AlertType.WARNING;
        MessageHelper.addMessage(model, alertType, message);

        verify(model, times(1)).addAttribute("type", alertType.getValue());
        verify(model, times(1)).addAttribute("error", message);
    }
}