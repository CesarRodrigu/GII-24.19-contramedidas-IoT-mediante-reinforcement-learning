package es.cesar.app.controller;

import es.cesar.app.model.User;
import es.cesar.app.service.LocaleFormattingService;
import es.cesar.app.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("coverage")
class SignupControllerTest {
    final String signupViewName = "users/signup";
    final String signupUrl = "/signup";
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private LocaleFormattingService formattingService;

    @Test
    void testGetSignup() throws Exception {
        mockMvc.perform(get(signupUrl))
                .andExpect(status().isOk())
                .andExpect(view().name(signupViewName))
                .andExpect(model().attributeExists("signupForm"));
    }

    @Test
    void testPostSignup_UserAlreadyExists() throws Exception {
        final String username = "testUser";
        final String password = "testPass";
        when(userService.getUserByUsername(username)).thenReturn(new User(username, password));
        when(formattingService.getMessage("manageusers.exists")).thenReturn("User already exists");
        mockMvc.perform(post(signupUrl)
                        .param("username", username)
                        .param("password", password)
                        .with(csrf())
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name(signupViewName))
                .andExpect(model().attributeHasFieldErrors("signupForm", "username"));
    }

    @Test
    void testPostSignup_SuccessfulRegistration() throws Exception {
        final String username = "testUser";
        final String password = "testPass";
        when(userService.getUserByUsername(username)).thenReturn(null);
        when(formattingService.getMessage("signup.successMessage")).thenReturn("User created successfully");

        mockMvc.perform(post(signupUrl)
                        .param("username", username)
                        .param("password", password)
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signin"));

        verify(userService, times(1)).save(any());
    }

    @Test
    void testPostSignup_WithValidationErrors() throws Exception {
        mockMvc.perform(post(signupUrl)
                        .param("username", "")
                        .param("password", "")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(signupViewName))
                .andExpect(model().attributeHasFieldErrors("signupForm", "username", "password"));
    }
}
