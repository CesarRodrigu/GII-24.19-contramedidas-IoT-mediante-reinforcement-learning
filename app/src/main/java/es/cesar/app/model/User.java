package es.cesar.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


/**
 * The entity class that represents a User.
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "password")
@Table(name = "users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @JsonIgnore
    private String password;

    private String firstName;
    private String lastName;

    private Instant created;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();


    /**
     * Instantiates a new User.
     *
     * @param username the username
     * @param password the password
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.created = Instant.now();
    }

    /**
     * Instantiates a new User.
     *
     * @param username the username
     * @param password the password
     * @param roles    the roles
     */
    public User(String username, String password, Collection<Role> roles) {
        this.username = username;
        this.password = password;
        this.created = Instant.now();
        this.roles.addAll(roles);
    }

    /**
     * Instantiates a new User.
     *
     * @param username the username
     * @param password the password
     * @param role     the role
     */
    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.created = Instant.now();
        this.roles.add(role);
    }
}