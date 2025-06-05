package es.cesar.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * The class MainController, that handles the main page requests.
 */
@Controller
public class MainController extends BaseController {
    /**
     * Gets the home page.
     *
     * @param modelMap the model map of the home page
     *
     * @return the path to the home view
     */
    @GetMapping("/")
    public String home(ModelMap modelMap) {
        setPage(modelMap);
        return "index";
    }
}
