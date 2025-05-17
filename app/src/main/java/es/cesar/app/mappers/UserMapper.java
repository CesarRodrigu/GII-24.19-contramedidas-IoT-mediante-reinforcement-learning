package es.cesar.app.mappers;

import es.cesar.app.dto.UserDto;
import es.cesar.app.model.Role;
import es.cesar.app.model.User;
import es.cesar.app.repository.RoleRepository;
import es.cesar.app.service.LocaleFormattingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.util.*;

@Component
public class UserMapper {

    private final LocaleFormattingService formattingService;
    private final RoleRepository roleRepository;


    @Autowired
    public UserMapper(LocaleFormattingService formattingService, RoleRepository roleRepository) {
        this.formattingService = formattingService;
        this.roleRepository = roleRepository;
    }

    public void updateEntity(UserDto dto, User user) {
        if (dto == null || user == null) return;

        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        HashSet<Role> roles = new HashSet<>();
        for (String roleName : dto.getRoles()) {
            Role role = roleRepository.findByRoleName(roleName);
            if (role != null) {
                roles.add(role);
            }
        }
        user.setRoles(roles);
        if (dto.getInstantCreated() != null) {
            user.setCreated(dto.getInstantCreated());
        }
    }

    public Collection<UserDto> toDtos(Collection<User> users) {
        return users.stream()
                .map(this::toDto)
                .toList();
    }

    public UserDto toDto(User user) {
        if (user == null) return null;

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRoles(user.getRoles().stream()
                .map(Role::getRoleName)
                .toList());

        if (user.getCreated() != null) {
            dto.setCreated(formattingService.formatDateTime(Date.from(user.getCreated()), DateFormat.SHORT, DateFormat.SHORT));
            dto.setInstantCreated(user.getCreated());
        }

        return dto;
    }

    public boolean isDifferent(UserDto dto, User user) {
        if (dto == null || user == null) return false;

        return !dto.getUsername().equals(user.getUsername()) ||
                !dto.getFirstName().equals(user.getFirstName()) ||
                !dto.getLastName().equals(user.getLastName()) ||
                isDifferent(dto.getRoles(), user.getRoles());
    }

    private boolean isDifferent(List<String> rolesDto, Set<Role> roles) {
        ArrayList<String> roleNames = new ArrayList<>();
        for (Role role : roles) {
            roleNames.add(role.getRoleName());
        }
        return !rolesDto.equals(roleNames);
    }
}
