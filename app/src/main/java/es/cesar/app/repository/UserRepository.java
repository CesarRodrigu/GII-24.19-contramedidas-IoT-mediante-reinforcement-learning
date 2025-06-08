package es.cesar.app.repository;

import es.cesar.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The interface UserRepository, that extends JpaRepository for User entity.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Find by username user.
     *
     * @param username the username
     *
     * @return the user
     */
    User findByUsername(String username);
}