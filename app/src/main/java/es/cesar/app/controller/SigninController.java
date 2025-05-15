package es.cesar.app.controller;

import es.cesar.app.dto.LoginForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SigninController extends BaseController {
    private static final String SIGNIN_VIEW_NAME = "users/signin";

    public SigninController() {
        super.module = "signin";
    }

    @GetMapping(value = "/login")
    public String signin(ModelMap interfazConPantalla) {
        interfazConPantalla.addAttribute("loginForm", new LoginForm());
        setPage(interfazConPantalla);
        return SIGNIN_VIEW_NAME;
    }
}