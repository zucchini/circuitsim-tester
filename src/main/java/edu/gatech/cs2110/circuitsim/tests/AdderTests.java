package edu.gatech.cs2110.circuitsim.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.ra4king.circuitsim.gui.peers.wiring.PinPeer;

import edu.gatech.cs2110.circuitsim.extensions.CircuitSimExtension;
import edu.gatech.cs2110.circuitsim.extensions.SubcircuitPin;
import edu.gatech.cs2110.circuitsim.extensions.SubcircuitTest;


@DisplayName("1-Bit Adder")
@ExtendWith(CircuitSimExtension.class)
@SubcircuitTest(file="adder.sim", subcircuit="1-bit adder")
public class AdderTests {
    @SubcircuitPin(input=true)
    private PinPeer a;

    @SubcircuitPin(input=true)
    private PinPeer b;

    @SubcircuitPin(input=true)
    private PinPeer cin;

    @SubcircuitPin(input=false)
    private PinPeer sum;

    @SubcircuitPin(input=false)
    private PinPeer cout;

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
