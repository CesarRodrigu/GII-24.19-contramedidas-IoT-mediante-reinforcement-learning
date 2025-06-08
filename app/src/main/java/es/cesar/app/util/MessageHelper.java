package es.cesar.app.util;

import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * The class MessageHelper, that provides utility methods for adding messages to the model or redirect attributes.
 */
public class MessageHelper {

    private MessageHelper() {
    }

    /**
     * Add flash message.
     *
     * @param ra      the redirect attributes
     * @param type    the alert type
     * @param message the message
     */
    public static void addFlashMessage(RedirectAttributes ra, AlertType type, String message) {
        ra.addFlashAttribute("type", type.getValue());
        ra.addFlashAttribute("error", message);
    }

    /**
     * Add message.
     *
     * @param model   the modelMap
     * @param type    the alert type
     * @param message the message
     */
    public static void addMessage(ModelMap model, AlertType type, String message) {
        model.addAttribute("type", type.getValue());
        model.addAttribute("error", message);
    }
}