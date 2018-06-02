package edu.gatech.cs2110.circuitsim.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Verifying Mathematics is Correct")
public class MathTests {
    @Test
    @DisplayName("2 + 3 = 5")
    void testMathIsReal() {
        assertEquals(5, 2 + 3);
    }

    @Test
    @DisplayName("2 + 2 = 5")
    void testMathIsFake() {
        assertEquals(5, 2 + 2);
    }
}
