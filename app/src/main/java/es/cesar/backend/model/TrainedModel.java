package es.cesar.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

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

    public void initializeWithFilename(User user, String fullFileName) {
        setUser(user);
        setFileName(fullFileName);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        this.name = removeExtension(fileName);
    }

    private String removeExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        this.extension = filename.substring(lastDot);
        return (lastDot > 0) ? filename.substring(0, lastDot) : filename;
    }

    public void initializeWithName(User user, String name) {
        setUser(user);
        this.name = name;
        if (this.extension == null) {
            this.extension = ".zip";
        }
        this.fileName = name + this.extension;
    }

    public Long getUserId() {
        return this.user.getId();
    }

    public void setName(String name) {
        this.name = name;
        this.fileName = name + this.extension;
    }
}
