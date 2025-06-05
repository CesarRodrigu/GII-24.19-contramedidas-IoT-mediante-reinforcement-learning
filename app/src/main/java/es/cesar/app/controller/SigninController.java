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

/**
 * The class SigninController, that handles the sign-in page requests.
 */
@Controller
public class SigninController extends BaseController {
    private static final String SIGNIN_VIEW_NAME = "users/signin";
    private final LocaleFormattingService formattingService;


    /**
     * Instantiates a new Signin controller.
     *
     * @param formattingService the formatting service
     */
    public SigninController(LocaleFormattingService formattingService) {
        super.module = "signin";
        this.formattingService = formattingService;
    }

    /**
     * Show the sign-in page.
     *
     * @param modelMap   the model map
     * @param redirected the redirected attribute, indicating if the user was redirected after a successful sign-up
     * @param ra         the redirect attributes
     *
     * @return the path to the sign-in view
     */
    @GetMapping(value = "/login")
    public String signin(ModelMap modelMap, @RequestParam(value = "redirected", required = false) Boolean redirected, RedirectAttributes ra) {
        modelMap.addAttribute("loginForm", new LoginForm());
        if (Boolean.TRUE.equals(redirected)) {
            MessageHelper.addMessage(modelMap, SUCCESS, formattingService.getMessage("signup.successMessage"));
        }
        setPage(modelMap);
        return SIGNIN_VIEW_NAME;
    }
}