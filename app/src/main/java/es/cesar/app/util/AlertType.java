package es.cesar.app.util;


import lombok.Getter;

/**
 * The enum AlertType, that represents the different types of alerts used by Boostrap 5 in the application.
 */
@Getter
public enum AlertType {
    /**
     * Primary alert type.
     */
    PRIMARY("primary"),
    /**
     * Secondary alert type.
     */
    SECONDARY("secondary"),
    /**
     * Success alert type.
     */
    SUCCESS("success"),
    /**
     * Danger alert type.
     */
    DANGER("danger"),
    /**
     * Warning alert type.
     */
    WARNING("warning"),
    /**
     * Info alert type.
     */
    INFO("info"),
    /**
     * Light alert type.
     */
    LIGHT("light"),
    /**
     * Dark alert type.
     */
    DARK("dark");

    private final String value;

    AlertType(String value) {
        this.value = value;
    }

}
