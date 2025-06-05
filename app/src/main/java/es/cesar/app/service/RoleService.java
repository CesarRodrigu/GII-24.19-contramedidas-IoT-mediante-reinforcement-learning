package es.cesar.app.service;

import es.cesar.app.model.Role;
import es.cesar.app.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * The class RoleService, that provides services related to roles.
 */
@Service
public class RoleService {
    private final RoleRepository roleRepository;

    /**
     * Instantiates a new Role service.
     *
     * @param roleRepository the role repository
     */
    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Gets all roles.
     *
     * @return the all roles
     */
    public Collection<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    /**
     * Gets all string roles.
     *
     * @return the all string roles
     */
    public Collection<String> getAllStringRoles() {
        return roleRepository.findAll().stream()
                .map(Role::getRoleName)
                .toList();
    }

}
