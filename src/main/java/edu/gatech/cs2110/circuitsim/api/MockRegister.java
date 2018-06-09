package edu.gatech.cs2110.circuitsim.api;

/**
 * Represents a "ghost" register: a register component replaced with Pin
 * components for easier testing. Useful for testing the combinational
 * logic in a sequential circuit by getting/setting the Pins contained
 * in it.
 * <p>
 * Idea: Suppose we want to test a circuit that looks like this
 * <pre>
 *                ______
 *       .-------|D    Q|------.
 *       |       |      |      |
 *       |       |_/\___|      |
 *       |                     |
 *       '--[ combinational ]--'
 *          [    logic      ]
 * </pre>
 * <p>
 * but we want to test that combinational logic — who cares about
 * the register? So rewire the circuit to look like this
 * instead, where d is an output pin, and q is an input pin:
 * <pre>
 *       .-------(d)  [q]------.
 *       |                     |
 *       |                     |
 *       |                     |
 *       '--[ combinational ]--'
 *          [    logic      ]
 * </pre>
 * <p>
 * This way, we can test that combinational logic on its own by
 * setting the value of q and then checking the value of d. In fact, a
 * {@code MockRegister} contains a new input/output pin for every
 * Register Port so you can test the student connected all register
 * ports correctly.
 *
 * @see Subcircuit#mockOnlyRegister(int)
 */
public class MockRegister {
    private InputPin q;
    private OutputPin d;
    private OutputPin en;
    private OutputPin clk;
    private OutputPin rst;
    private Subcircuit subcircuit;

    MockRegister(InputPin q, OutputPin d, OutputPin en, OutputPin clk,
                 OutputPin rst, Subcircuit subcircuit) {
        this.q = q;
        this.d = d;
        this.en = en;
        this.clk = clk;
        this.rst = rst;
        this.subcircuit = subcircuit;
    }

    /**
     * Returns the Pin which replaced the out port of the register.
     * This is the only input Pin in {@code MockRegister} since you
     * should {@link InputPin#set(int)} this to the value you want the
     * "ghost" register to contain in the test.
     *
     * @return the {@code InputPin} for the Q register port
     */
    public InputPin getQ() {
        return q;
    }

    /**
     * Returns the Pin which replaced the in port of the register.
     * This is an output Pin because the student's code will set it to
     * something.
     *
     * @return the {@code OutputPin} for the D register port
     */
    public OutputPin getD() {
        return d;
    }

    /**
     * Returns the Pin which replaced the write enable port of the
     * register.
     * This is an output Pin because the student's code will set it to
     * something.
     *
     * @return the {@code OutputPin} for the write enable register port
     */
    public OutputPin getEn() {
        return en;
    }

    /**
     * Returns the Pin which replaced the clock port of the register.
     * This is an output Pin because the student's code will set it to
     * something.
     *
     * @return the {@code OutputPin} for the clock register port
     */
    public OutputPin getClk() {
        return clk;
    }

    /**
     * Returns the Pin which replaced the reset port of the register.
     * This is an output Pin because the student's code will set it to
     * something.
     *
     * @return the {@code OutputPin} for the reset register port
     */
    public OutputPin getRst() {
        return rst;
    }


    /**
     * Returns the {@link Subcircuit} where the Pins which make up this
     * {@link MockRegister} live.
     *
     * @return the subcircuit where this mock register lives
     */
    public Subcircuit getSubcircuit() {
        return subcircuit;
    }
}
