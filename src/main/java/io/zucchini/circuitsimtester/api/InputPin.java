package io.zucchini.circuitsimtester.api;

import com.ra4king.circuitsim.simulator.WireValue;
import com.ra4king.circuitsim.simulator.components.wiring.Pin;

/**
 * Wraps a CircuitSim input {@code Pin} component and provides a
 * convenience method for setting its value.
 */
public class InputPin extends BasePin {
    /**
     * Creates a new InputPin which wraps the provided {@code Pin}
     * component and which lives in the provided {@code Subcircuit}.
     *
     * @param pin {@code Pin} component to wrap
     * @param subcircuit where this pin lives
     * @see   BasePin#BasePin(Pin, Subcircuit)
     */
    public InputPin(Pin pin, Subcircuit subcircuit) {
        super(pin, subcircuit);
    }

    /**
     * Sets the value of this input pin and allows the change to
     * propagate through the circuit.
     *
     * @param value what to set the pin to. Negative numbers are fine.
     */
    public void set(int value) {
        pin.setValue(subcircuit.getCircuitState(), WireValue.of(value, pin.getBitSize()));
        subcircuit.getSimulator().stepAll();
    }
}
