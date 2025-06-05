package es.cesar.app.controller;

import es.cesar.app.dto.SignupForm;
import es.cesar.app.service.LocaleFormattingService;
import es.cesar.app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * The class SignupController, that handles the sign-up page requests.
 */
@Controller
public class SignupController extends BaseController {

    private static final String SIGNUP_VIEW_NAME = "users/signup";
    private final UserService userService;
    private final LocaleFormattingService formattingService;


    /**
     * Instantiates a new Signup controller.
     *
     * @param userService       the user service
     * @param formattingService the formatting service
     */
    @Autowired
    public SignupController(UserService userService, LocaleFormattingService formattingService) {
        this.userService = userService;
        this.formattingService = formattingService;
        super.module = "signup";
    }

    /**
     * Show the sign-up page.
     *
     * @param modelMap the model map
     *
     * @return the path to the sign-up view
     */
    @GetMapping("/signup")
    public String signup(ModelMap modelMap) {
        modelMap.addAttribute(new SignupForm());
        setPage(modelMap);
        return SIGNUP_VIEW_NAME;
    }

    /**
     * Handles the sign-up form submission.
     *
     * @param signupForm the signup form
     * @param errors     the errors
     * @param ra         the redirect attributes
     *
     * @return the path to the sign-up view
     */
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

        return "redirect:/login?redirected=true";
    }
}