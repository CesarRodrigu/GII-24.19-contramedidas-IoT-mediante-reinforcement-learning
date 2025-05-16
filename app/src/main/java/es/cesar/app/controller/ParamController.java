package es.cesar.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ParamController extends BaseController {
    public ParamController() {
        super.module = "params";
    }

    @GetMapping("/params")
    public String params(ModelMap interfazConPantalla) {
        setPage(interfazConPantalla);
        return "params/Params";
    }
}
