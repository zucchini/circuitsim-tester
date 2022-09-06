package io.zucchini.circuitsimtester.api;

import com.ra4king.circuitsim.simulator.components.memory.ROM;

/**
 * Wraps a CircuitSim ROM component.
 *
 * @author Austin Adams
 */
public class Rom extends BaseMemory {
    private ROM rom;
    private Subcircuit subcircuit;

    /**
     * Creates a new Rom which wraps the provided {@code ROM}
     * component and which lives in the provided {@code Subcircuit}.
     *
     * @param rom {@code ROM} component to wrap
     * @param subcircuit where this pin lives
     */
    public Rom(ROM rom, Subcircuit subcircuit) {
        this.rom = rom;
        this.subcircuit = subcircuit;
    }

    @Override
    public void store(int address, int value) {
        rom.getMemory()[address] = value;
        subcircuit.getCircuit().forEachState(state -> rom.valueChanged(state, null, 0));
    }

    /**
     * Returns the internal CircuitSim {@code ROM} component this
     * object wraps.
     * <p>
     * <b>This exposes an internal CircuitSim API. Do not use unless you
     *    know what you are doing.</b>
     *
     * @return the CircuitSim {@code ROM} component wrapped by this
     *         object.
     */
    public ROM getROM() {
        return rom;
    }

    /**
     * Returns the {@link Subcircuit}, a wrapper around a CircuitSim
     * {@code CircuitBoard} where this Pin lives.
     *
     * @return the {@link Subcircuit} where this pin lives
     */
    public Subcircuit getSubcircuit() {
        return subcircuit;
    }
}
