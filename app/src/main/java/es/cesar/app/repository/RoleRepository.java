package es.cesar.app.repository;

import es.cesar.app.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The interface RoleRepository, that extends JpaRepository for Role entity.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {
    /**
     * Find by role name role.
     *
     * @param roleName the role name
     *
     * @return the role
     */
    Role findByRoleName(String roleName);
}