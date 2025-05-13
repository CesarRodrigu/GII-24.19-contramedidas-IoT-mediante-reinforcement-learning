package es.cesar.backend.service;

import es.cesar.backend.config.details.CustomUserDetails;
import es.cesar.backend.model.Role;
import es.cesar.backend.model.User;
import es.cesar.backend.repository.RoleRepository;
import es.cesar.backend.repository.TrainedModelRepository;
import es.cesar.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private TrainedModelRepository trainedModelRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        roleRepository = mock(RoleRepository.class);
        trainedModelRepository = mock(TrainedModelRepository.class);
        passwordEncoder = mock(BCryptPasswordEncoder.class);
        userService = new UserService(userRepository, roleRepository, trainedModelRepository, passwordEncoder);
    }

    @Test
    void testSaveUser_PasswordIsEncoded() {
        final String testPassword = "rawPassword";
        final String testPasswordEncoded = "encodedPassword";
        User user = new User();
        user.setPassword(testPassword);

        when(passwordEncoder.encode(testPassword)).thenReturn(testPasswordEncoded);

        userService.save(user);

        assertEquals(testPasswordEncoded, user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void testGetAllUsers_ReturnsAllUsers() {
        List<User> users = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(users.size(), result.size());
    }

    @Test
    void testGetUserById_UserExists() {
        final Long userId = 1L;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.getUserById(userId);

        assertEquals(user, result);
    }

    @Test
    void testGetUserById_UserDoesNotExist() {
        final Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        User result = userService.getUserById(userId);

        assertNull(result);
    }

    @Test
    void testGetUserByUsername() {
        final String username = "username";
        User user = new User();
        when(userRepository.findByUsername(username)).thenReturn(user);

        User result = userService.getUserByUsername(username);

        assertEquals(user, result);
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.updateUser(user);

        assertEquals(user, result);
    }

    @Test
    void testDeleteUserById() {
        final Long userId = 1L;
        User user = new User();
        Role role = new Role();
        role.setUsers(new HashSet<>(Set.of(user)));
        user.setRoles(Set.of(role));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUserById(userId);

        assertFalse(role.getUsers().contains(user));
        verify(roleRepository).saveAll(anyList());
        verify(trainedModelRepository).deleteAllByUser(user);
        verify(userRepository).delete(user);
    }

    @Test
    void testLoadUserByUsername_UserExists() {
        final String username = "testUser";
        final String password = "testPassword";
        final String roleName = "ROLE_USER";

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        Role role = new Role();
        role.setRoleName(roleName);
        user.setRoles(Set.of(role));

        when(userRepository.findByUsername(username)).thenReturn(user);

        CustomUserDetails userDetails = userService.loadUserByUsername(username);

        assertEquals(username, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals(roleName)));
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        final String username = "test";

        when(userRepository.findByUsername(username)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(username));
    }
}
