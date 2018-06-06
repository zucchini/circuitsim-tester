package edu.gatech.cs2110.circuitsim.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import edu.gatech.cs2110.circuitsim.extension.CircuitSimExtension;
import edu.gatech.cs2110.circuitsim.extension.InputPin;
import edu.gatech.cs2110.circuitsim.extension.OutputPin;
import edu.gatech.cs2110.circuitsim.extension.SubcircuitPin;
import edu.gatech.cs2110.circuitsim.extension.SubcircuitTest;

@DisplayName("1-Bit Adder")
@ExtendWith(CircuitSimExtension.class)
@SubcircuitTest(file="adder.sim", subcircuit="1-bit adder")
public class AdderTests {
    @SubcircuitPin(bits = 1)
    private InputPin a;

    @SubcircuitPin(bits = 1)
    private InputPin b;

    @SubcircuitPin(bits = 1)
    private InputPin cin;

    @SubcircuitPin(bits = 1)
    private OutputPin sum;

    @SubcircuitPin(bits = 1)
    private OutputPin cout;

    @Test
    @DisplayName("0 + 0 + 0 = 0")
    public void allZeroesIsZero() {
        a.set(0);
        b.set(0);
        cin.set(0);
        assertEquals(0, sum.get());
        assertEquals(0, cout.get());
    }

    @Test
    @DisplayName("1 + 1 + 1 = 3")
    public void allOnesIsThree() {
        a.set(1);
        b.set(1);
        cin.set(1);
        assertEquals(1, sum.get());
        assertEquals(1, cout.get());
    }
}
