package es.cesar.app.controller;

import es.cesar.app.dto.SignupForm;
import es.cesar.app.service.LocaleFormattingService;
import es.cesar.app.service.UserService;
import es.cesar.app.util.MessageHelper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static es.cesar.app.util.AlertType.SUCCESS;

@Controller
public class SignupController extends BaseController {

    private static final String SIGNUP_VIEW_NAME = "users/signup";
    private final UserService userService;
    private final LocaleFormattingService formattingService;


    @Autowired
    public SignupController(UserService userService, LocaleFormattingService formattingService) {
        this.userService = userService;
        this.formattingService = formattingService;
        super.module = "signup";
    }

    @GetMapping("/signup")
    public String signup(ModelMap interfazConPantalla) {
        interfazConPantalla.addAttribute(new SignupForm());
        setPage(interfazConPantalla);
        return SIGNUP_VIEW_NAME;
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute SignupForm signupForm, Errors errors, RedirectAttributes ra) {
        if (errors.hasErrors()) {
            return SIGNUP_VIEW_NAME;
        }
        if (userService.getUserByUsername(signupForm.getUsername()) != null) {
            errors.rejectValue("username", "error.username", formattingService.getMessage("manageusers.exists"));
            return SIGNUP_VIEW_NAME;
        }
        userService.save(signupForm.createUser());

        MessageHelper.addFlashMessage(ra, SUCCESS, formattingService.getMessage("signup.successMessage"));
        return "redirect:/signin";
    }
}