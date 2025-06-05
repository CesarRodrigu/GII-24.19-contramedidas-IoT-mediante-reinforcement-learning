package es.cesar.app.service;

import es.cesar.app.config.details.CustomUserDetails;
import es.cesar.app.model.Role;
import es.cesar.app.model.User;
import es.cesar.app.repository.RoleRepository;
import es.cesar.app.repository.TrainedModelRepository;
import es.cesar.app.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * The class UserService, that provides services related to users.
 */
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TrainedModelRepository trainedModelRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Instantiates a new User service.
     *
     * @param userRepository         the user repository
     * @param roleRepository         the role repository
     * @param trainedModelRepository the trained model repository
     * @param passwordEncoder        the password encoder
     */
    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, TrainedModelRepository trainedModelRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.trainedModelRepository = trainedModelRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Save user.
     *
     * @param user the user
     *
     * @return the user
     */
    @Transactional
    public User save(@NotNull User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return user;
    }

    /**
     * Gets all users.
     *
     * @return the all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Gets user by id.
     *
     * @param id the id
     *
     * @return the user by id
     */
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Gets user by username.
     *
     * @param username the username
     *
     * @return the user by username
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }


    /**
     * Update user user.
     *
     * @param updatedUser the updated user
     *
     * @return the user
     */
    public User updateUser(User updatedUser) {
        return userRepository.save(updatedUser);
    }

    /**
     * Delete user by id.
     *
     * @param id the id
     */
    @Transactional
    public void deleteUserById(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            List<Role> rolesToUpdate = new ArrayList<>(user.getRoles());
            for (Role role : rolesToUpdate) {
                role.getUsers().remove(user);
            }
            roleRepository.saveAll(rolesToUpdate);

            trainedModelRepository.deleteAllByUser(user);

            userRepository.delete(user);
        });
    }

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return new CustomUserDetails(username, user.getPassword(), mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRoleName())).toList();
    }
}
