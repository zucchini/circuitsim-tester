package edu.gatech.cs2110.circuitsim.extension;

import com.ra4king.circuitsim.simulator.components.wiring.Pin;

abstract class BasePin {
    protected Pin pin;
    protected Subcircuit subcircuit;

    BasePin(Pin pin, Subcircuit subcircuit) {
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
