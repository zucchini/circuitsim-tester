package io.zucchini.circuitsimtester.api;

import static com.ra4king.circuitsim.simulator.components.memory.Register.PORT_OUT;

/**
 * Wraps a CircuitSim Register component. Currently changing the value
 * of the register is not supported, but you can observe its value.
 *
 * @author Austin Adams
 */
public class Register {
    private com.ra4king.circuitsim.simulator.components.memory.Register reg;
    private Subcircuit subcircuit;

    /**
     * Creates a new Register which wraps the provided {@code Register}
     * component and which lives in the provided {@code Subcircuit}.
     *
     * @param reg {@code Register} component to wrap
     * @param subcircuit where this pin lives
     */
    public Register(com.ra4king.circuitsim.simulator.components.memory.Register reg,
                    Subcircuit subcircuit) {
        this.reg = reg;
        this.subcircuit = subcircuit;
    }

    /**
     * Returns the current value of the register. This value is not
     * sign-extended; to sign-extend the value, see {@link getQSext()}.
     *
     * @return the current value of the register, not sign extended
     */
    public int getQ() {
        return Bits.betterFloatingErrorMessage(() ->
            subcircuit.getCircuitState().getLastPushed(reg.getPort(PORT_OUT)).getValue());
    }

    /**
     * Returns the current value of the register, sign-extended to 32
     * bits. Like {@link getQ()} except sign-extended.
     *
     * @return the current value of the register, sign extended to 32 bits
     */
    public int getQSext() {
        return Bits.sext(getQ(), reg.getBitSize());
    }

    /**
     * Returns the internal CircuitSim {@code Register} component this
     * object wraps.
     * <p>
     * <b>This exposes an internal CircuitSim API. Do not use unless you
     *    know what you are doing.</b>
     *
     * @return the CircuitSim {@code Register} component wrapped by this
     *         object.
     */
    public com.ra4king.circuitsim.simulator.components.memory.Register getRegister() {
        return reg;
    }

    /**
     * Mocks this register with a {@link MockRegister}. See {@link
     * MockRegister} and {@link
     * Subcircuit#mockRegister(com.ra4king.circuitsim.simulator.components.memory.Register)}
     * for details.
     * <p>
     * You probably <b>should not be calling this directly</b> — use the
     * following code instead and let {@link
     * io.zucchini.circuitsimtester.extension.CircuitSimExtension} handle this
     * for you:
     * <pre>
     * {@literal @}SubcircuitComponent(bits=16)
     * private MockRegister myFunRegister;
     * </pre>
     *
     * @return a mocked version of this register
     * @see MockRegister
     * @see Subcircuit#mockRegister(com.ra4king.circuitsim.simulator.components.memory.Register)
     */
    public MockRegister mock() {
        return subcircuit.mockRegister(reg);
    }

    /**
     * Returns the {@link Subcircuit}, a wrapper around a CircuitSim
     * {@code CircuitBoard} where this Register lives.
     *
     * @return the {@link Subcircuit} where this pin lives
     */
    public Subcircuit getSubcircuit() {
        return subcircuit;
    }
}
