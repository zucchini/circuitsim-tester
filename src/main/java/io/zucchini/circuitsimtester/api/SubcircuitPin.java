package io.zucchini.circuitsimtester.api;

import io.zucchini.circuitsimtester.extension.CircuitSimExtension;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

/**
 * Instructs {@link CircuitSimExtension}
 * to find and inject the Pin component into an {@link InputPin} or
 * {@link OutputPin} field in a test class.
 * <p>
 * When you annotate an {@link InputPin} or {@link OutputPin} field in a
 * test class with this, {@link CircuitSimExtension}
 * will search the subcircuit for a Pin component with direction
 * (input/output) matching the type of the field, bitsize {@code
 * bits()}, and label {@code label()}. If {@code label()} is {@code ""}
 * (the default), it will search for a label matching the name of the
 * field instead.
 *
 * @see Subcircuit#lookupPin(String,boolean,int)
 * @see InputPin
 * @see OutputPin
 */
@Retention(RUNTIME)
public @interface SubcircuitPin {
    /**
     * The label of a matching Pin. If empty (the default), {@link
     * CircuitSimExtension} will
     * use the name of the field.
     *
     * @return The label of a matching Pin.
     */
    String label() default ""; // label of pin (default: name of variable)

    /**
     * The bit size of a matching Pin.
     *
     * @return The bit size of a matching Pin.
     */
    int bits();
}
