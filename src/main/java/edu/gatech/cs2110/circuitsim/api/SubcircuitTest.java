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
     * Validate the subcircuit with these {@link Restrictor}s before
     * running any tests. Useful for checking for banned gates.
     * <p>
     * The idiom is to subclass {@link Restrictor} inside your test
     * class and then call {@link Restrictor} methods as needed inside
     * its {@code validate()}, such as
     * {@link Restrictor#whitelistComponents
     * Restrictor.whitelistComponents()} or
     * {@link Restrictor#blacklistComponents Restrictor.blacklistComponents()}
     * as follows:
     * <pre>
     * {@literal @}DisplayName("Toy ALU")
     * {@literal @}ExtendWith(CircuitSimExtension.class)
     * {@literal @}SubcircuitTest(file="toy-alu.sim", subcircuit="ALU",
     *                 restrictors={ToyALUTests.BannedGates.class})
     * public class ToyALUTests {
     *     public static class BannedGates extends Restrictor {
     *         {@literal @}Override
     *         public void validate(Subcircuit subcircuit) throws AssertionError {
     *             blacklistComponents(subcircuit, "XOR");
     *         }
     *     }
     *
     *     // ...
     * }
     * </pre>
     *
     * The default is an empty array, so to perform no validation.
     *
     * @return restrictor classes to use to validate the subcircuit
     * @see Restrictor
     */
    Class<? extends Restrictor>[] restrictors() default {};

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
