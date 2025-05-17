package es.cesar.app.util;

import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class MessageHelper {

    private MessageHelper() {
    }

    public static void addFlashMessage(RedirectAttributes ra, AlertType type, String message) {
        ra.addFlashAttribute("type", type.getValue());
        ra.addFlashAttribute("error", message);
    }

    public static void addMessage(ModelMap model, AlertType type, String message) {
        model.addAttribute("type", type.getValue());
        model.addAttribute("error", message);
    }
}