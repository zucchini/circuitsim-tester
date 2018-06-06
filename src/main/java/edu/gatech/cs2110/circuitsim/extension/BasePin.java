package edu.gatech.cs2110.circuitsim.extension;

import com.ra4king.circuitsim.simulator.components.wiring.Pin;

abstract class BasePin {
    protected Pin pin;

    BasePin(Pin pin) {
        this.pin = pin;
    }

    public Pin getPin() {
        return pin;
    }
}
