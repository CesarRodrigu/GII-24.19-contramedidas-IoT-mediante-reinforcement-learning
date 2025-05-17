package es.cesar.app.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TupleTest {
    @Test
    void testTuple() {
        final String item1 = "test";
        final Integer item2 = 1;
        Tuple<String, Integer> tuple = new Tuple<>(item1, item2);
        assertEquals(item1, tuple.getLeft(), "Left item should be " + item1);
        assertEquals(item2, tuple.getRight(), "Right item should be " + item2);
    }

    @Test
    void testTupleEquality() {
        final String item1 = "test";
        final Integer item2 = 1;
        Tuple<String, Integer> tuple1 = new Tuple<>(item1, item2);
        Tuple<String, Integer> tuple2 = new Tuple<>(item1, item2);
        assertEquals(tuple1, tuple2, "Tuples should be equal");
    }

    @Test
    void testTupleInequality() {
        final String item1 = "test";
        final Integer item2 = 1;
        final String item3 = "test2";
        Tuple<String, Integer> tuple1 = new Tuple<>(item1, item2);
        Tuple<String, Integer> tuple2 = new Tuple<>(item3, item2);
        assertNotEquals(tuple1, tuple2, "Tuples should not be equal");
    }

}