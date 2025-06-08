package es.cesar.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * The class ParamController, that handles the parameters page requests.
 */
@Controller
public class ParamController extends BaseController {
    /**
     * Instantiates a new Param controller.
     */
    public ParamController() {
        super.module = "params";
    }

    /**
     * Provides the page of parameters.
     *
     * @param modelMap the model map
     *
     * @return the path to the parameters view
     */
    @GetMapping("/params")
    public String params(ModelMap modelMap) {
        setPage(modelMap);
        return "params/Params";
    }
}
