package es.cesar.app.controller;

import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;

public abstract class BaseController {
    private static final String ATTRIBUTE_NAME = "module";
    protected String module = "home";

    protected void setPage(ModelMap model) {
        model.addAttribute(ATTRIBUTE_NAME, module);
    }

    protected void setPage(Model model) {
        model.addAttribute(ATTRIBUTE_NAME, module);
    }
}
