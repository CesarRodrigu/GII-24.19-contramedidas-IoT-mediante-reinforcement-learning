package es.cesar.backend.service;

import es.cesar.backend.model.Role;
import es.cesar.backend.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class RoleServiceTest {

    private RoleRepository roleRepository;
    private RoleService roleService;

    @BeforeEach
    void setUp() {
        roleRepository = mock(RoleRepository.class);
        roleService = new RoleService(roleRepository);
    }

    @Test
    void testGetAllRoles() {
        List<String> expectedRolesNames = Arrays.asList("ROLE_ADMIN", "ROLE_USER");

        List<Role> roles = new ArrayList<>();

        for (String roleName : expectedRolesNames) {
            Role role = new Role();
            role.setRoleName(roleName);
            roles.add(role);
        }


        when(roleRepository.findAll()).thenReturn(roles);

        Collection<Role> result = roleService.getAllRoles();

        assertEquals(roles.size(), result.size(), "Role list size does not match expected value");
        for (Role expectedRole : roles) {
            assertTrue(result.contains(expectedRole), "Expected role not found: " + expectedRole);

        }
        verify(roleRepository, times(1)).findAll();
    }

    @Test
    void testGetAllStringRoles() {
        List<String> expectedRoleNames = Arrays.asList("ADMIN", "USER");

        List<Role> roles = new ArrayList<>();
        for (String roleName : expectedRoleNames) {
            Role role = new Role();
            role.setRoleName(roleName);
            roles.add(role);
        }

        when(roleRepository.findAll()).thenReturn(roles);

        Collection<String> result = roleService.getAllStringRoles();

        assertEquals(expectedRoleNames.size(), result.size(), "Role list size does not match expected value");
        for (String expectedName : expectedRoleNames) {
            assertTrue(result.contains(expectedName), "Expected role name not found: " + expectedName);
        }

        verify(roleRepository, times(1)).findAll();
    }

}
