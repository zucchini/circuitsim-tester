package io.zucchini.circuitsimtester.api;

import io.zucchini.circuitsimtester.extension.CircuitSimExtension;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

/**
 * Instructs {@link CircuitSimExtension} to find and inject a CircuitSim
 * component into the annotated field in a test class.
 * <p>
 * The annotated field can be a {@link InputPin}, {@link OutputPin}, {@link
 * Register}, {@link MockRegister}, {@link Ram}, {@link Rom}, {@link Clock}, or
 * {@link Button}.
 * <p>
 * When you annotate such a field in a test class with this annotation, {@link
 * CircuitSimExtension} will search the subcircuit for a suitable component
 * matching the requirements specified by the parameters to this annotation.
 * <p>
 * If {@link #label()} is {@code ""} (the default), it will search for a label
 * matching the name of the field instead. Also, note that {@link #bits()} is
 * required for some components.
 *
 * @see Subcircuit#lookupPin(String,boolean,int,boolean)
 * @see Subcircuit#lookupRegister(String,int,boolean)
 * @see Subcircuit#lookupMemory(String,int,boolean,MemoryType)
 * @see Subcircuit#mockRegister(com.ra4king.circuitsim.simulator.components.memory.Register)
 * @see Subcircuit#mockPulser(String,boolean,PulserType)
 */
@Retention(RUNTIME)
public @interface SubcircuitComponent {
    /**
     * The label of a matching Pin. If empty (the default), {@link
     * CircuitSimExtension} will use the name of the field.
     *
     * @return The label of a matching Pin.
     */
    String label() default ""; // label of pin (default: name of variable)

    /**
     * The bit size of a matching Pin.
     * <p>
     * This parameter <b>must be provided</b> for {@link InputPin}s, {@link
     * OutputPin}s, {@link Register}s, {@link MockRegister}s, {@link Ram}s, and
     * {@link Rom}s.
     * <p>
     * This parameter <b>must NOT be provided</b> for {@link Clock}s and {@link
     * Button}s.
     *
     * @return The bit size of the desired compoment.
     */
    int bits() default -1;

    /**
     * Used to tell the tester library not to look up the component by label
     * and instead to find this component by looking for the only instance of
     * such component present in the circuit (optionally recursively via {@link
     * #recursiveSearch()}).
     *
     * @return true if this should be the only component of its type present in
     *         the circuit (e.g., the only register), else false
     */
    boolean onlyInstance() default false;

    /**
     * Search for this component not only directly in the subcircuit identified
     * with {@link SubcircuitTest} but also in any subcircuits placed in it.
     * <p>
     * For example, if you are autograding a simple finite state machine
     * homework and a student is a subcircuit enthusiast and places their
     * actual register deep in many levels of subcircuits, this parameter
     * would tell the autograder to go subcircuit spelunking to find the
     * register.
     *
     * @return true to look for the desired component in subcircuits placed in
     *         this subcircuit, else false
     */
    boolean recursiveSearch() default false;

    /**
     * For {@link OutputPin}s: The type of "pin". The default is a literal
     * existing Pin component, but you can set this to {@code TUNNEL} to spy on
     * the value of a tunnel by attaching an {@link OutputPin} to it. (The
     * label is then the label of the tunnel you want to spy instead of the
     * label of an existing pin.)
     *
     * This parameter is valid <b>only</b> for {@link OutputPin}s.
     *
     * @return An enum value determining if this should spy on a tunnel
     *         instead of controlling a Pin component
     */
    Type type() default Type.INFER;

    /**
     * Please see {@link SubcircuitComponent#type()} for details on this enum.
     *
     * @see SubcircuitComponent#type()
     */
    enum Type {
        /**
         * Infer the type of component based on the annotated field type. This
         * is the default behavior.
         */
        INFER,
        /**
         * For {@link OutputPin}s: snitch on a tunnel instead of trying to find
         * an output pin.
         *
         * @see SubcircuitComponent#type()
         */
        TUNNEL,
    }
}
