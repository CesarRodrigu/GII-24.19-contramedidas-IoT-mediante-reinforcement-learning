package es.cesar.app.mappers;

import es.cesar.app.dto.UserDto;
import es.cesar.app.model.Role;
import es.cesar.app.model.User;
import es.cesar.app.repository.RoleRepository;
import es.cesar.app.service.LocaleFormattingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserMapperTest {
    private RoleRepository roleRepository;
    private LocaleFormattingService formattingService;
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        roleRepository = mock(RoleRepository.class);
        formattingService = mock(LocaleFormattingService.class);
        userMapper = new UserMapper(formattingService, roleRepository);
    }

    @Test
    void testUpdateEntity() {
        UserDto dto = new UserDto();
        final String userName = "userName";
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String role1 = "ADMIN";
        final String role2 = "USER";

        dto.setUsername(userName);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setRoles(List.of(role1, role2));
        dto.setInstantCreated(Instant.now());

        Role adminRole = new Role();
        adminRole.setRoleName(role1);
        Role userRole = new Role();
        userRole.setRoleName(role2);

        when(roleRepository.findByRoleName(role1)).thenReturn(adminRole);
        when(roleRepository.findByRoleName(role2)).thenReturn(userRole);

        User user = new User();
        userMapper.updateEntity(dto, user);

        assertEquals(userName, user.getUsername(), "Username was not updated correctly.");
        assertEquals(firstName, user.getFirstName(), "First name was not updated correctly.");
        assertEquals(lastName, user.getLastName(), "Last name was not updated correctly.");
        assertEquals(2, user.getRoles().size(), "Roles count mismatch.");
        assertTrue(user.getRoles().contains(adminRole), role1 + " role not assigned.");
        assertTrue(user.getRoles().contains(userRole), role2 + " role not assigned.");
        assertEquals(dto.getInstantCreated(), user.getCreated(), "Created timestamp mismatch.");
    }

    @Test
    void testToDto() {
        final Long id = 1L;
        final String userName = "userName";
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String roleName = "USER";
        final Instant created = Instant.now();
        final String formattedDate = "12/12/2023";

        User user = new User();
        user.setId(id);
        user.setUsername(userName);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setCreated(created);

        Role role = new Role();
        role.setRoleName(roleName);
        user.setRoles(Set.of(role));

        when(formattingService.formatDateTime(any(), anyInt(), anyInt())).thenReturn(formattedDate);

        UserDto dto = userMapper.toDto(user);

        assertNotNull(dto, "UserDto should not be null.");
        assertEquals(id, dto.getId(), "ID mismatch.");
        assertEquals(userName, dto.getUsername(), "Username mismatch.");
        assertEquals(firstName, dto.getFirstName(), "First name mismatch.");
        assertEquals(lastName, dto.getLastName(), "Last name mismatch.");
        assertEquals(List.of(roleName), dto.getRoles(), "Roles mismatch.");
        assertEquals(formattedDate, dto.getCreated(), "Formatted date mismatch.");
        assertEquals(created, dto.getInstantCreated(), "InstantCreated timestamp mismatch.");
    }

    @Test
    void testToDtos() {
        final String name1 = "a";
        final String name2 = "b";

        User user1 = new User();
        user1.setUsername(name1);
        User user2 = new User();
        user2.setUsername(name2);

        List<User> users = List.of(user1, user2);
        Collection<UserDto> dtos = userMapper.toDtos(users);

        assertEquals(2, dtos.size(), "DTO collection size mismatch.");
    }

    @Test
    void testWrongParameters() {
        User user = new User();
        UserDto dto = new UserDto();
        dto.setRoles(List.of("NOT_EXISTING_ROLE"));
        assertDoesNotThrow(() -> userMapper.updateEntity(dto, user), "Should not throw exception.");

    }

    @Test
    void testIsDifferent_shouldReturnFalse_whenAllFieldsMatch() {
        final String username = "username";
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String roleName = "USER";

        Role role = new Role();
        role.setRoleName(roleName);
        Set<Role> roles = Set.of(role);

        User user = new User();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRoles(roles);

        UserDto dto = new UserDto();
        dto.setUsername(username);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setRoles(List.of(roleName));

        assertFalse(userMapper.isDifferent(dto, user), "Should return false when all fields match.");
    }

    @Test
    void testIsDifferent_shouldReturnTrue_whenUsernameDiffers() {
        final String username1 = "username1";
        final String username2 = "username2";
        final String sameFirst = "sameFirst";
        final String sameLast = "sameLast";

        User user = new User();
        user.setUsername(username1);
        user.setFirstName(sameFirst);
        user.setLastName(sameLast);
        user.setRoles(Set.of());

        UserDto dto = new UserDto();
        dto.setUsername(username2);
        dto.setFirstName(sameFirst);
        dto.setLastName(sameLast);
        dto.setRoles(List.of());

        assertTrue(userMapper.isDifferent(dto, user), "Should detect username difference.");
    }

    @Test
    void testIsDifferent_shouldReturnTrue_whenRolesDiffer() {
        final String username = "username";
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String role1 = "USER";
        final String role2 = "ADMIN";

        Role role = new Role();
        role.setRoleName(role1);

        User user = new User();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRoles(Set.of(role));

        UserDto dto = new UserDto();
        dto.setUsername(username);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setRoles(List.of(role2));

        assertTrue(userMapper.isDifferent(dto, user), "Should detect role mismatch.");
    }

    @Test
    void testToDto_nullInput() {
        assertNull(userMapper.toDto(null), "Expected null when input User is null.");
    }

    @Test
    void testUpdateEntity_nullInput() {
        User user = new User();
        UserDto dto = new UserDto();

        assertDoesNotThrow(() -> userMapper.updateEntity(null, user), "Should not throw when DTO is null.");
        assertDoesNotThrow(() -> userMapper.updateEntity(dto, null), "Should not throw when User is null.");
    }

    @Test
    void testIsDifferent_nullInput() {
        assertFalse(userMapper.isDifferent(null, new User()), "Should return false when DTO is null.");
        assertFalse(userMapper.isDifferent(new UserDto(), null), "Should return false when User is null.");
        assertFalse(userMapper.isDifferent(null, null), "Should return false when User and Dto are null.");

    }

    @Test
    void testIsDifferent_shouldReturnFalse_whenFirstAndLastNameAreEqual() {
        final String username = "user";
        final String name = "Name";
        final String last = "Last";

        User user = new User();
        user.setUsername(username);
        user.setFirstName(name);
        user.setLastName(last);
        user.setRoles(Set.of());

        UserDto dto = new UserDto();
        dto.setUsername(username);
        dto.setFirstName(name);
        dto.setLastName(last);
        dto.setRoles(List.of());

        assertFalse(userMapper.isDifferent(dto, user), "Should return false when first and last names are equal.");
    }

    @Test
    void testIsDifferent_shouldReturnTrue_whenFirstNameDiffers() {
        final String username = "user";
        final String nameUser = "Name1";
        final String nameDto = "Name2";
        final String last = "Last";

        User user = new User();
        user.setUsername(username);
        user.setFirstName(nameUser);
        user.setLastName(last);
        user.setRoles(Set.of());

        UserDto dto = new UserDto();
        dto.setUsername(username);
        dto.setFirstName(nameDto);
        dto.setLastName(last);
        dto.setRoles(List.of());

        assertTrue(userMapper.isDifferent(dto, user), "Should return true when first name differs.");
    }

    @Test
    void testIsDifferent_shouldReturnTrue_whenLastNameDiffers() {
        final String username = "user";
        final String name = "Name";
        final String lastUser = "Last1";
        final String lastDto = "Last2";

        User user = new User();
        user.setUsername(username);
        user.setFirstName(name);
        user.setLastName(lastUser);
        user.setRoles(Set.of());

        UserDto dto = new UserDto();
        dto.setUsername(username);
        dto.setFirstName(name);
        dto.setLastName(lastDto);
        dto.setRoles(List.of());

        assertTrue(userMapper.isDifferent(dto, user), "Should return true when last name differs.");
    }

    @Test
    void testIsDifferent_shouldReturnTrue_whenFirstAndLastNameDiffer() {
        final String username = "user";
        final String nameUser = "First1";
        final String nameDto = "First2";
        final String lastUser = "Last1";
        final String lastDto = "Last2";

        User user = new User();
        user.setUsername(username);
        user.setFirstName(nameUser);
        user.setLastName(lastUser);
        user.setRoles(Set.of());

        UserDto dto = new UserDto();
        dto.setUsername(username);
        dto.setFirstName(nameDto);
        dto.setLastName(lastDto);
        dto.setRoles(List.of());

        assertTrue(userMapper.isDifferent(dto, user), "Should return true when both first and last names differ.");
    }

}