package io.zucchini.circuitsimtester.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validates a subcircuit by checking for issues such as banned
 * components. Example:
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
 * @see SubcircuitTest#restrictors
 */
public abstract class Restrictor {
    /**
     * Validates this subcircuit, throwing an {@code AssertionError} if
     * any issues are found. (The subcircuit name will automatically be
     * included in the exception, so don't worry about that.) Subclasses
     * should override this method with one that calls the protected
     * helper methods in this class as needed.
     *
     * @param subcircuit the subcircuit to validate
     * @throws AssertionError on validation error. Do not worry about
     *                        including the subcircuit name
     */
    public abstract void validate(Subcircuit subcircuit) throws AssertionError;

    /**
     * Fail the whole test if the subcircuit contains any components other than
     * these components or categories.
     * <p>
     * Automatically includes Input Pins, Output Pins, Constants,
     * Tunnels, Probes, and Text. But other components will be allowed only if
     * you specify them here, including other Wiring components. Please
     * consider starting off with {@code "Wiring", "Text"}, or you will
     * risk frustrating students.
     *
     * @param subcircuit Subcircuit to validate
     * @param componentNames component names or category names to whitelist
     */
    protected void whitelistComponents(Subcircuit subcircuit, String... componentNames)
            throws AssertionError {
        checkForBannedComponents(subcircuit, Arrays.asList(componentNames), true);
    }

    /**
     * Fail the whole test if the subcircuit contains any of these components
     * or component categories.
     *
     * @param subcircuit Subcircuit to validate
     * @param componentNames component names or category names to blacklist
     */
    protected void blacklistComponents(Subcircuit subcircuit, String... componentNames)
            throws AssertionError {
        checkForBannedComponents(subcircuit, Arrays.asList(componentNames), false);
    }

    private void checkForBannedComponents(Subcircuit subcircuit,
                                          Collection<String> componentNames,
                                          boolean isWhitelist) {
        // These should always be included, but TAs might not think to
        // include them.
        if (isWhitelist) {
            List<String> componentNamesFixed = new LinkedList<>(componentNames);
            componentNamesFixed.add("Input Pin");
            componentNamesFixed.add("Output Pin");
            componentNamesFixed.add("Constant");
            componentNamesFixed.add("Tunnel");
            componentNamesFixed.add("Text");
            componentNamesFixed.add("Probe");
            componentNames = componentNamesFixed;
        }

        Set<String> violatingComponentNames =
            subcircuit.lookupComponents(componentNames, isWhitelist);

        if (!violatingComponentNames.isEmpty()) {
            throw new AssertionError(String.format(
                "contains banned components: %s. It " +
                "could contain these banned components indirectly; double-check " +
                "subcircuits placed in it as well.",
                violatingComponentNames.stream().map(name -> String.format("`%s'", name))
                                       .collect(Collectors.joining(", "))));
        }
    }
}
