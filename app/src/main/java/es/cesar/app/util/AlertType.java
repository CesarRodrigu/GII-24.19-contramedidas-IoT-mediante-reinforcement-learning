package es.cesar.app.util;


import lombok.Getter;

@Getter
public enum AlertType {
    PRIMARY("primary"),
    SECONDARY("secondary"),
    SUCCESS("success"),
    DANGER("danger"),
    WARNING("warning"),
    INFO("info"),
    LIGHT("light"),
    DARK("dark");

    private final String value;

    AlertType(String value) {
        this.value = value;
    }

}
