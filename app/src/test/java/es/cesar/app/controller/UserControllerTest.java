package es.cesar.app.controller;

import es.cesar.app.dto.UserDto;
import es.cesar.app.model.User;
import es.cesar.app.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("coverage")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testManageUsers() throws Exception {
        mockMvc.perform(get("/admin/manageUsers"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/users"))
                .andExpect(model().attributeExists("userDtoList"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testEditUser_NotFound() throws Exception {
        mockMvc.perform(get("/admin/editUser").param("id", "999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/manageUsers"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testEditUser() throws Exception {
        final long id = 1;
        Mockito.when(userService.getUserById(id)).thenReturn(new User());
        UserDto userDto = new UserDto();
        List<String> roles = List.of();
        userDto.setRoles(roles);

        mockMvc.perform(get("/admin/editUser").param("id", String.valueOf(id)))
                .andExpect(status().isOk())
                .andExpect(view().name("users/edit"))
                .andExpect(model().attribute("availableRoles", roles))
                .andExpect(model().attribute("userDto", userDto));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testDeleteUser() throws Exception {
        mockMvc.perform(post("/admin/deleteUser").with(csrf())
                        .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/manageUsers"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testUpdateUser() throws Exception {
        mockMvc.perform(post("/admin/updateUser").with(csrf())
                        .param("id", "1")
                        .param("username", "testuser"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/manageUsers"));
    }

    @Test
    @WithMockUser(roles = "TEST")
    void testLogout() throws Exception {
        mockMvc.perform(post("/logout").with(csrf()).param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"));
    }
}