package io.zucchini.circuitsimtester.api;

import com.ra4king.circuitsim.simulator.components.wiring.Pin;

/**
 * Wraps a CircuitSim Pin component. You shouldn't need to use this
 * directly; {@link InputPin} and {@link OutputPin} extend this class
 * and provide methods for getting or setting the value of the pin.
 *
 * @see InputPin
 * @see OutputPin
 * @author Austin Adams
 */
public abstract class BasePin {
    /**
     * The {@code Pin} wrapped by this instance.
     */
    protected Pin pin;
    /**
     * The {@link Subcircuit} where this pin lives
     */
    protected Subcircuit subcircuit;

    /**
     * Creates a new BasePin which wraps the provided {@code Pin}
     * component and which lives in the provided {@code Subcircuit}.
     *
     * @param pin {@code Pin} component to wrap
     * @param subcircuit where this pin lives
     */
    public BasePin(Pin pin, Subcircuit subcircuit) {
        this.pin = pin;
        this.subcircuit = subcircuit;
    }

    /**
     * Returns the internal CircuitSim {@code Pin} component this
     * object wraps.
     * <p>
     * <b>This exposes an internal CircuitSim API. Do not use unless you
     *    know what you are doing.</b>
     *
     * @see    InputPin#set(int)
     * @see    OutputPin#get()
     * @return the CircuitSim {@code Pin} component wrapped by this
     *         object.
     */
    public Pin getPin() {
        return pin;
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
