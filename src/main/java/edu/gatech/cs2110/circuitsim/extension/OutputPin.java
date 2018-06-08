package edu.gatech.cs2110.circuitsim.extension;

import com.ra4king.circuitsim.simulator.components.wiring.Pin;

public class OutputPin extends BasePin {
    public OutputPin(Pin pin, Subcircuit subcircuit) {
        super(pin, subcircuit);
    }

    public int get() {
        try {
            return subcircuit.getCircuitState().getLastReceived(pin.getPort(Pin.PORT)).getValue();
        } catch (IllegalStateException err) {
            // An IllegalStateException is thrown when at least one bit
            // is floating. But it just says "Invalid value," so throw a
            // more human-friendly error message
            throw new IllegalStateException(
                "At least one output bit is floating (undefined, or blue in CircuitSim). " +
                "Is the output pin connected to anything?");
        }
    }
}
