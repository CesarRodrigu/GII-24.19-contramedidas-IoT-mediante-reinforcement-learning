package es.cesar.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * The entity class that represents a Role.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "roles")
public class Role implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String roleName;
    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();

    /**
     * Instantiates a new Role.
     *
     * @param roleName the role name
     */
    public Role(String roleName) {
        this.roleName = roleName;
    }

}