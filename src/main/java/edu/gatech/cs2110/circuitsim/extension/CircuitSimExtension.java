package edu.gatech.cs2110.circuitsim.extension;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.Extension;

import edu.gatech.cs2110.circuitsim.api.BasePin;
import edu.gatech.cs2110.circuitsim.api.InputPin;
import edu.gatech.cs2110.circuitsim.api.MockRegister;
import edu.gatech.cs2110.circuitsim.api.OutputPin;
import edu.gatech.cs2110.circuitsim.api.Subcircuit;
import edu.gatech.cs2110.circuitsim.api.SubcircuitPin;
import edu.gatech.cs2110.circuitsim.api.SubcircuitRegister;
import edu.gatech.cs2110.circuitsim.api.SubcircuitTest;

/**
 * Extends JUnit to understand testing CircuitSim subcircuits.
 * <p>
 * To use: annotate a JUnit test class with {@code  @ExtendWith(CircuitSimExtension.class)}
 *
 * @see <a href="https://github.com/ausbin/circuitsim-grader-template/blob/master/README.md">The README with examples</a>
 */
public class CircuitSimExtension implements Extension, BeforeAllCallback, BeforeEachCallback {
    private List<FieldInjection> fieldInjections;
    private Subcircuit subcircuit;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        Class<?> testClass = context.getRequiredTestClass();
        SubcircuitTest subcircuitAnnotation = testClass.getAnnotation(SubcircuitTest.class);

        if (subcircuitAnnotation == null) {
            throw new IllegalArgumentException(
                "The CircuitSim extension requires annotating the test class with @SubcircuitTest");
        }

        subcircuit = Subcircuit.fromPath(subcircuitAnnotation.file(),
                                         subcircuitAnnotation.subcircuit());
        checkForBannedComponents(subcircuitAnnotation);
        fieldInjections = new LinkedList<>();
        fieldInjections.addAll(generatePinFieldInjections(testClass));
        fieldInjections.addAll(generateRegFieldInjections(testClass));
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        // Reset simulator before each test
        subcircuit.resetSimulation();

        // Do simple dependency injection
        for (FieldInjection fieldInjection : fieldInjections) {
            fieldInjection.inject(extensionContext.getRequiredTestInstance());
        }
    }

    private void checkForBannedComponents(SubcircuitTest subcircuitAnnotation) {
        boolean hasComponentBlacklist =
            subcircuitAnnotation.blacklistedComponents().length > 0;
        boolean hasComponentWhitelist =
            subcircuitAnnotation.whitelistedComponents().length > 0;

        if (hasComponentBlacklist && hasComponentWhitelist) {
            throw new IllegalArgumentException(
                "blacklistedComponents and whitelistedComponents are mutually exclusive");
        }

        if (hasComponentBlacklist || hasComponentWhitelist) {
            List<String> restrictedComponents = new LinkedList<>(Arrays.asList(
                hasComponentBlacklist? subcircuitAnnotation.blacklistedComponents()
                                     : subcircuitAnnotation.whitelistedComponents()));

            // These should always be included, but TAs might not think to
            // include them.
            if (hasComponentWhitelist) {
                restrictedComponents.add("Input Pin");
                restrictedComponents.add("Output Pin");
                restrictedComponents.add("Constant");
                restrictedComponents.add("Tunnel");
            }

            Set<String> violatingComponentNames =
                subcircuit.lookupComponents(restrictedComponents,
                                            hasComponentWhitelist);

            if (!violatingComponentNames.isEmpty()) {
                throw new IllegalArgumentException(String.format(
                    "The subcircuit `%s' contains banned components: %s. It " +
                    "could contain these banned components indirectly; double-check " +
                    "subcircuits placed in it as well.",
                    subcircuit.getName(),
                    violatingComponentNames.stream().map(name -> String.format("`%s'", name))
                                           .collect(Collectors.joining(", "))));
            }
        }
    }

    private Collection<FieldInjection> generatePinFieldInjections(Class<?> testClass) {
        List<FieldInjection> fieldInjections = new LinkedList<>();

        List<Field> pinFields = Arrays.stream(testClass.getDeclaredFields())
                                      .filter(field -> field.isAnnotationPresent(SubcircuitPin.class))
                                      .collect(Collectors.toList());

        // TODO: Detect duplicate pins (`Pin a' and `Pin A' are distinct
        //       fields in Java but not here). Easy solution: sort the
        //       stream by canonicalName(field.getName()) and iterate
        //       over the resulting List, comparing each with the
        //       next

        for (Field pinField : pinFields) {
            if (!BasePin.class.isAssignableFrom(pinField.getType())) {
                throw new IllegalArgumentException(
                    "Test class fields annotated with @SubcircuitPin should be of type InputPin or OutputPin");
            }

            boolean wantInputPin = InputPin.class.isAssignableFrom(pinField.getType());
            SubcircuitPin pinAnnotation = pinField.getDeclaredAnnotation(SubcircuitPin.class);
            String pinLabel = pinAnnotation.label().isEmpty()? pinField.getName() : pinAnnotation.label();

            BasePin pinWrapper = subcircuit.lookupPin(pinLabel, wantInputPin, pinAnnotation.bits());
            fieldInjections.add(new FieldInjection(pinField, pinWrapper));
        }

        return fieldInjections;
    }

    private Collection<FieldInjection> generateRegFieldInjections(Class<?> testClass) {
        List<FieldInjection> fieldInjections = new LinkedList<>();

        List<Field> regFields = Arrays.stream(testClass.getDeclaredFields())
                                      .filter(field -> field.isAnnotationPresent(SubcircuitRegister.class))
                                      .collect(Collectors.toList());

        // TODO: Like in generatePinFieldInjections(), search for dupe
        //       fields in the class itself

        for (Field regField : regFields) {
            if (!MockRegister.class.isAssignableFrom(regField.getType())) {
                throw new IllegalArgumentException(
                    "Test class fields annotated with @SubcircuitRegister should be of type MockRegister");
            }

            SubcircuitRegister regAnnotation = regField.getDeclaredAnnotation(SubcircuitRegister.class);

            // I added this field to the annotation so that we don't
            // break existing tests down the road if we implement this
            if (!regAnnotation.onlyRegister()) {
                throw new UnsupportedOperationException(
                    "@SubcircuitRegister(onlyRegister=false, ...) is not yet supported. " +
                    "(Note: the default is onlyRegister=false, so you'll need to write onlyRegister=true.)");
            }

            MockRegister reg = subcircuit.mockOnlyRegister(regAnnotation.bits());
            fieldInjections.add(new FieldInjection(regField, reg));
        }

        return fieldInjections;
    }

    private static class FieldInjection {
        private Field field;
        private Object valueToInject;

        public FieldInjection(Field field, Object valueToInject) {
            this.field = field;
            this.valueToInject = valueToInject;
        }

        public void inject(Object obj) throws IllegalAccessException {
            field.setAccessible(true);
            field.set(obj, valueToInject);
        }
    }
}
