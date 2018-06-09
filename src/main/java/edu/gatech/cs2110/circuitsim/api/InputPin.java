package edu.gatech.cs2110.circuitsim.api;

import com.ra4king.circuitsim.simulator.WireValue;
import com.ra4king.circuitsim.simulator.components.wiring.Pin;

public class InputPin extends BasePin {
    public InputPin(Pin pin, Subcircuit subcircuit) {
        super(pin, subcircuit);
    }

    public int get() {
        return subcircuit.getCircuitState().getLastPushed(pin.getPort(Pin.PORT)).getValue();
    }

    public void set(int value) {
        pin.setValue(subcircuit.getCircuitState(), WireValue.of(value, pin.getBitSize()));
        subcircuit.getSimulator().stepAll();
    }
}
