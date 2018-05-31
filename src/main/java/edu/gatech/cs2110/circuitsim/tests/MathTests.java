package edu.gatech.cs2110.circuitsim.tests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MathTests {
    @Test
    void testMathIsReal() {
        assertEquals(5, 2 + 3);
    }

    @Test
    void testMathIsFake() {
        assertEquals(5, 2 + 2);
    }
}
