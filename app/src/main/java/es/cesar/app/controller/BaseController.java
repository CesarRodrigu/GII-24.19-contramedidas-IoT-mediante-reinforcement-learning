package es.cesar.app.controller;

import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;

/**
 * The abstract class BaseController, that provides common functionality for all controllers.
 */
public abstract class BaseController {
    private static final String ATTRIBUTE_NAME = "module";
    /**
     * The base module name.
     */
    protected String module = "home";

    /**
     * Sets the attribute of the module to the page.
     *
     * @param model the modelMap
     */
    protected void setPage(ModelMap model) {
        model.addAttribute(ATTRIBUTE_NAME, module);
    }

    /**
     * Sets the attribute of the module to the page.
     *
     * @param model the model
     */
    protected void setPage(Model model) {
        model.addAttribute(ATTRIBUTE_NAME, module);
    }
}
