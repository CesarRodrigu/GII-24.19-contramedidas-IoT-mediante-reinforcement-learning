package es.cesar.backend.service;

import es.cesar.backend.model.Role;
import es.cesar.backend.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Collection<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Collection<String> getAllStringRoles() {
        return roleRepository.findAll().stream()
                .map(Role::getRoleName)
                .toList();
    }

}
