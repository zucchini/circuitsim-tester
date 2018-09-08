package io.zucchini.circuitsimtester.api;

import io.zucchini.circuitsimtester.extension.CircuitSimExtension;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

/**
 * Instructs {@link CircuitSimExtension}
 * to find and inject a {@link MockRegister} for a Register component
 * into a {@link MockRegister} field in a test class.
 * <p>
 * When you annotate a {@link MockRegister} field in a test class with
 * this, {@link CircuitSimExtension}
 * will use {@link Subcircuit#mockOnlyRegister(int)} to replace the only
 * register in the file with a "ghost register." See {@link
 * MockRegister} for details.
 *
 * @see MockRegister
 * @see Subcircuit#mockOnlyRegister(int)
 */
@Retention(RUNTIME)
public @interface SubcircuitRegister {
    /**
     * Whether to match the only register in the subcircuit.
     *
     * @return true if you want the only register in the file, false
     * otherwise (default but currently not supported).
     */
    boolean onlyRegister() default false;

    /**
     * Desired bitsize of a matching register.
     *
     * @return bitsize of a matching register
     */
    int bits();
}
