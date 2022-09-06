package io.zucchini.circuitsimtester.api;

/**
 * Allows pretending to push a button placed in a subcircuit.
 * <p>
 * (In reality, this manipulates an InputPin wired to where the button was;
 * see {@link MockPulser})
 */
public class Button extends MockPulser {
    public Button(InputPin mockPin, Subcircuit subcircuit) {
        super(mockPin, subcircuit);
    }

    /**
     * This pushes the button. Crazy, right?
     */
    public void press() {
        pulse();
    }
}
