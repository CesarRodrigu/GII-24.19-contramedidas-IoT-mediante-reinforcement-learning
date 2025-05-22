package es.cesar.app.controller;

import es.cesar.app.dto.LoginForm;
import es.cesar.app.service.LocaleFormattingService;
import es.cesar.app.util.MessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static es.cesar.app.util.AlertType.SUCCESS;

@Controller
public class SigninController extends BaseController {
    private static final String SIGNIN_VIEW_NAME = "users/signin";
    private final LocaleFormattingService formattingService;


    public SigninController(LocaleFormattingService formattingService) {
        super.module = "signin";
        this.formattingService = formattingService;
    }

    @GetMapping(value = "/login")
    public String signin(ModelMap interfazConPantalla, @RequestParam(value = "redirected", required = false) Boolean redirected, RedirectAttributes ra) {
        interfazConPantalla.addAttribute("loginForm", new LoginForm());
        if (Boolean.TRUE.equals(redirected)) {
            MessageHelper.addMessage(interfazConPantalla, SUCCESS, formattingService.getMessage("signup.successMessage"));
        }
        setPage(interfazConPantalla);
        return SIGNIN_VIEW_NAME;
    }
}