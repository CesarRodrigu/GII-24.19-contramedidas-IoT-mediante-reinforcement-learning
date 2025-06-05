package es.cesar.app.controller;

import es.cesar.app.dto.UserDto;
import es.cesar.app.mappers.UserMapper;
import es.cesar.app.model.User;
import es.cesar.app.service.LocaleFormattingService;
import es.cesar.app.service.RoleService;
import es.cesar.app.service.UserService;
import es.cesar.app.util.MessageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static es.cesar.app.util.AlertType.*;


/**
 * The class UserController, that handles user-related requests.
 */
@Controller
public class UserController extends BaseController {
    private static final String MANAGE_USERS_VIEW_NAME = "users/users";
    private static final String MANAGE_USERS_VIEW_URL = "admin/manageUsers";
    private static final String EDIT_USERS_VIEW_NAME = "users/edit";
    private static final String REDIRECT = "redirect:/";
    private final UserService userService;
    private final RoleService roleService;
    private final UserMapper userMapper;
    private final LocaleFormattingService formattingService;


    /**
     * Instantiates a new User controller.
     *
     * @param userService       the user service
     * @param roleService       the role service
     * @param userMapper        the user mapper
     * @param formattingService the formatting service
     */
    @Autowired
    public UserController(UserService userService, RoleService roleService, UserMapper userMapper, LocaleFormattingService formattingService) {
        this.userService = userService;
        this.roleService = roleService;
        this.userMapper = userMapper;
        this.formattingService = formattingService;
        super.module = "manage_users";
    }

    /**
     * Handle the request to manage users.
     *
     * @param modelMap the model map
     *
     * @return the path to the manage users view
     */
    @GetMapping(MANAGE_USERS_VIEW_URL)
    public String manageUsers(ModelMap modelMap) {
        List<User> userList = userService.getAllUsers();
        modelMap.addAttribute("userDtoList", userMapper.toDtos(userList));
        setPage(modelMap);
        return MANAGE_USERS_VIEW_NAME;
    }

    /**
     * Handle the request to delete a user.
     *
     * @param id                 the id of the user
     * @param redirectAttributes the redirect attributes
     *
     * @return the path to the manage users view or redirect URL
     */
    @RequestMapping(value = "/admin/deleteUser", method = {RequestMethod.POST, RequestMethod.DELETE})
    public String deleteUser(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        userService.deleteUserById(id);

        MessageHelper.addFlashMessage(redirectAttributes, SUCCESS, formattingService.getMessage("manageusers.delete.success"));
        return REDIRECT + MANAGE_USERS_VIEW_URL;
    }

    /**
     * Handle the request to edit a user.
     *
     * @param id                 the id of the user
     * @param modelMap           the model map
     * @param redirectAttributes the redirect attributes
     *
     * @return the path to the edit user view or redirect URL
     */
    @GetMapping("/admin/editUser")
    public String editUser(@RequestParam Long id, ModelMap modelMap, RedirectAttributes redirectAttributes) {
        User user = userService.getUserById(id);
        if (user == null) {
            MessageHelper.addFlashMessage(redirectAttributes, DANGER, formattingService.getMessage("manageusers.notfound"));
            return REDIRECT + MANAGE_USERS_VIEW_URL;
        }
        modelMap.addAttribute("availableRoles", roleService.getAllStringRoles());
        modelMap.addAttribute("userDto", userMapper.toDto(user));
        setPage(modelMap);
        return EDIT_USERS_VIEW_NAME;
    }

    /**
     * Handle the request to update a user.
     *
     * @param userDto            the user dto
     * @param redirectAttributes the redirect attributes
     *
     * @return the path to the manage users view or redirect URL
     */
    @RequestMapping(value = "/admin/updateUser", method = {RequestMethod.POST, RequestMethod.PUT})
    public String updateUser(@ModelAttribute UserDto userDto, RedirectAttributes redirectAttributes) {
        User user = userService.getUserById(userDto.getId());
        if (!userMapper.isDifferent(userDto, user)) {
            MessageHelper.addFlashMessage(redirectAttributes, WARNING, formattingService.getMessage("manageusers.update.nochange"));
            return REDIRECT + MANAGE_USERS_VIEW_URL;
        }
        userMapper.updateEntity(userDto, user);
        userService.updateUser(user);
        MessageHelper.addFlashMessage(redirectAttributes, INFO, formattingService.getMessage("manageusers.update.success"));
        return REDIRECT + MANAGE_USERS_VIEW_URL;
    }

    /**
     * Handle the request to logout.
     *
     * @param id the id of the user
     *
     * @return the path to the redirect URL
     */
    @PostMapping("/logout")
    public String logout(@RequestParam Long id) {
        return REDIRECT;
    }
}
