package edu.gatech.cs2110.circuitsim.api;

import com.ra4king.circuitsim.simulator.components.wiring.Pin;

public abstract class BasePin {
    protected Pin pin;
    protected Subcircuit subcircuit;

    public BasePin(Pin pin, Subcircuit subcircuit) {
        this.pin = pin;
        this.subcircuit = subcircuit;
    }

    public Pin getPin() {
        return pin;
    }

    public Subcircuit getSubcircuit() {
        return subcircuit;
    }
}
