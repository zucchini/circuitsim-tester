package edu.gatech.cs2110.circuitsim.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import edu.gatech.cs2110.circuitsim.extensions.CircuitSimExtension;
import edu.gatech.cs2110.circuitsim.extensions.SubcircuitTest;

@DisplayName("1-Bit Adder")
@ExtendWith(CircuitSimExtension.class)
@SubcircuitTest(file="adder.sim", subcircuit="1-bit adder")
public class AdderTests {
    @Test
    @DisplayName("0 + 0 + 0 = 0")
    public void allZeroesIsZero() {
        assertEquals(0, 0 + 0 + 0);
    }

    @Test
    @DisplayName("1 + 1 + 1 = 3")
    public void allOnesIsThree() {
        assertEquals(3, 1 + 1 + 1);
    }
}
