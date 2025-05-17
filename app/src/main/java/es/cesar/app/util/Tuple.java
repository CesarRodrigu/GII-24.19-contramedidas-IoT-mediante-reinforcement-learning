package es.cesar.app.util;

import lombok.Data;

@Data
public class Tuple<L, R> {
    private final L left;
    private final R right;
}
