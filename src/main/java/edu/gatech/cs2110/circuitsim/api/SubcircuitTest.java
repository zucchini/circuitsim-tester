package edu.gatech.cs2110.circuitsim.api;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

/**
 * Marks this JUnit test class as testing a subcircuit with the provided
 * {@code .sim} file path and subcircuit name.
 *
 * @see Subcircuit#fromPath(String,String)
 */
@Retention(RUNTIME)
public @interface SubcircuitTest {
    /**
     * The circuit file to open. Usually a relative path.
     *
     * @return the path of a {@code .sim} file
     * @see Subcircuit#fromPath(String,String)
     */
    String file();

    /**
     * The name of the subcircuit in which to search for pins.
     * Normalized as described in {@link Subcircuit#fromPath(String,String)}.
     *
     * @return the name of the subcircuit. Normalized before lookup
     * @see Subcircuit#fromPath(String,String)
     */
    String subcircuit();

    /**
     * Fail the whole test if the subcircuit contains any of these components
     * or component categories.
     * Mutually exclusive with {@link #whitelistedComponents()}.
     *
     * @return list of banned component names or component category names
     */
    String[] blacklistedComponents() default {};

    /**
     * Fail the whole test if the subcircuit contains any components other than
     * these components or categories.
     * Mutually exclusive with {@link #blacklistedComponents()}.
     * <p>
     * Automatically includes Input Pins, Output Pins, Constants, and
     * Tunnels. always allowed. But other components will be allowed
     * only if you specify them here, including other Wiring components.
     * Please consider starting off with {@code
     * whitelistedComponents={"Wiring"}}, or you will risk frustrating
     * students.
     *
     * @return list of required component names or component category names
     */
    String[] whitelistedComponents() default {};

    /**
     * Reset simulation between tests.
     * <p>
     * Defaults to false because of possible performance (OOM) issues
     * with this behavior.
     *
     * @return true if the simulation should be reset before each test,
     *         false if the simulation should never be reset
     * @see Subcircuit#resetSimulation()
     */
    boolean resetSimulationBetween() default false;
}
