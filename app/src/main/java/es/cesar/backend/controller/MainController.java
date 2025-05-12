package es.cesar.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController extends BaseController {
    @GetMapping("/")
    public String vistaHome(ModelMap interfazConPantalla) {
        setPage(interfazConPantalla);
        return "index";
    }
}
