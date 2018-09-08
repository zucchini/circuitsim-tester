package io.zucchini.circuitsimtester.api;

import com.ra4king.circuitsim.simulator.components.wiring.Pin;

/**
 * Wraps a CircuitSim output {@code Pin} component and provides a
 * convenience method for setting its value.
 */
public class OutputPin extends BasePin {
    /**
     * Creates a new OutputPin which wraps the provided {@code Pin}
     * component and which lives in the provided {@code Subcircuit}.
     *
     * @param pin {@code Pin} component to wrap
     * @param subcircuit where this pin lives
     * @see   BasePin#BasePin(Pin, Subcircuit)
     */
    public OutputPin(Pin pin, Subcircuit subcircuit) {
        super(pin, subcircuit);
    }

    /**
     * Returns the current value of this output pin. Does <b>not</b>
     * sign extend (see {@link getSext} for that).
     *
     * @return value of this pin
     * @throws IllegalStateException If any bit is floating
     * @see    getSext
     */
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

    /**
     * Returns the current value of this output pin sign-extended to 32
     * bits.
     *
     * @return value of this pin sign-extended to 32 bits
     * @throws IllegalStateException If any bit is floating
     * @see    get
     */
    public int getSext() {
        int got = get();
        int bits = pin.getBitSize();

        // x << 32 == x if x is a java int, so exclude that case
        if (bits < 32 && (got & (1 << (bits - 1))) != 0) {
            return got | (-1 << bits);
        } else {
            return got;
        }
    }
}
