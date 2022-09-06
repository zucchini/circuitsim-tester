package io.zucchini.circuitsimtester.api;

/**
 * Similar to {@link MockRegister} but much simpler: manipulates an InputPin
 * which replaced a clock or button and is used to send "pulses" into the
 * circuit. By "pulse", I mean an input like this:
 * <pre>
 * 1            _
 *             | |
 *             | |
 *             | |
 * 0 __________| |___________
 *
 *         Time ---&gt;
 * </pre>
 * Notice that could be a button press or the rising edge of the clock signal
 * (in CircuitSim we do not have to worry about propagation delay, thank God),
 * hence this is a base class used for {@link Button} and {@link Clock}. Those
 * classes have their own more specialized methods for sending pulses that
 * ultimately call {@link #pulse()}.
 */
public abstract class MockPulser {
    protected InputPin mockPin;
    protected Subcircuit subcircuit;

    public MockPulser(InputPin mockPin, Subcircuit subcircuit) {
        this.mockPin = mockPin;
        this.subcircuit = subcircuit;
    }

    public InputPin getMockPin() {
        return mockPin;
    }

    public Subcircuit getSubcircuit() {
        return subcircuit;
    }

    /**
     * Set the mock pin to 0, then 1, then 0 again to simulate the "pulse"
     * drawn above in the lede.
     */
    protected void pulse() {
        mockPin.set(0b0);
        mockPin.set(0b1);
        mockPin.set(0b0);
    }
}
