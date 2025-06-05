package es.cesar.app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * The entity class that represents a TrainedModel.
 */
@Data
@NoArgsConstructor
@Entity
@ToString(exclude = "user")
public class TrainedModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long modelId;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String fileName;

    @Lob
    private byte[] file;

    @Lob
    private String description;

    private String extension;

    /**
     * Initialize with filename.
     *
     * @param user         the user
     * @param fullFileName the full file name
     */
    public void initializeWithFilename(User user, String fullFileName) {
        setUser(user);
        setFileName(fullFileName);
    }

    /**
     * Sets file name.
     *
     * @param fileName the file name
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
        this.name = removeExtension(fileName);
    }

    String removeExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1) {
            return filename;
        }
        this.extension = filename.substring(lastDot);
        return lastDot > 0 ? filename.substring(0, lastDot) : filename;
    }

    /**
     * Initialize with name.
     *
     * @param user the user
     * @param name the name
     */
    public void initializeWithName(User user, String name) {
        setUser(user);
        this.name = name;
        if (this.extension == null) {
            this.extension = ".zip";
        }
        this.fileName = name + this.extension;
    }

    /**
     * Gets user id.
     *
     * @return the user id
     */
    public Long getUserId() {
        return this.user.getId();
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
        this.fileName = name + this.extension;
    }
}
