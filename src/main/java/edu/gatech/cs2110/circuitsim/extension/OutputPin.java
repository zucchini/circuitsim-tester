package edu.gatech.cs2110.circuitsim.extension;

import com.ra4king.circuitsim.simulator.components.wiring.Pin;

public class OutputPin extends BasePin {
    public OutputPin(Pin pin, Subcircuit subcircuit) {
        super(pin, subcircuit);
    }

    public int get() {
        return subcircuit.getCircuitState().getLastReceived(pin.getPort(Pin.PORT)).getValue();
    }
}
