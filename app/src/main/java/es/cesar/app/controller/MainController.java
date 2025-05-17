package es.cesar.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController extends BaseController {
    @GetMapping("/")
    public String home(ModelMap interfazConPantalla) {
        setPage(interfazConPantalla);
        return "index";
    }
}
