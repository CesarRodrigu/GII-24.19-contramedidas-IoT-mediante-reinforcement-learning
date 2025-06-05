package es.cesar.app.util;

import lombok.Data;

/**
 * The class Tuple, that represents a pair of values.
 *
 * @param <L> the type parameter
 * @param <R> the type parameter
 */
@Data
public class Tuple<L, R> {
    private final L left;
    private final R right;
}
