package edu.gatech.cs2110.circuitsim.extensions;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ra4king.circuitsim.gui.CircuitSim;
import com.ra4king.circuitsim.gui.CircuitBoard;
import com.ra4king.circuitsim.gui.Properties;
import com.ra4king.circuitsim.gui.peers.wiring.PinPeer;
//import com.ra4king.circuitsim.simulator.Circuit;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

public class CircuitSimExtension implements Extension, BeforeAllCallback, BeforeEachCallback,
                                            ParameterResolver {
    private List<FieldInjection> fieldInjections;
    // TODO: Put all this info about subcircuit state into a new class
    //       called SubcircuitState or something, which we can pass into
    //       wrappers like Input/OutputPin. Should be much cleaner
    private CircuitBoard circuitBoard;
    private CircuitSim circuitSim;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        Class<?> testClass = context.getRequiredTestClass();
        SubcircuitTest subcircuitAnnotation = testClass.getAnnotation(SubcircuitTest.class);

        if (subcircuitAnnotation == null) {
            throw new IllegalArgumentException(
                "The CircuitSim extension requires annotating the test class with @SubcircuitTest");
        }

        File circuitFile = new File(subcircuitAnnotation.file());

        // In theory, zucchini should guarantee this for us, but stuff
        // happens
        if (!circuitFile.canRead()) {
            throw new IllegalArgumentException(
                String.format("Cannot read circuit file `%s'", subcircuitAnnotation.file()));
        }

        circuitSim = new CircuitSim(false);
        circuitSim.loadCircuits(circuitFile);
        circuitBoard = lookupSubcircuit(circuitSim, subcircuitAnnotation.subcircuit());

        fieldInjections = new LinkedList<>();
        fieldInjections.addAll(lookupPins(circuitBoard, testClass));
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        // Reset simulator before each test
        circuitSim.getSimulator().reset();

        // Do simple dependency injection
        for (FieldInjection fieldInjection : fieldInjections) {
            fieldInjection.inject(extensionContext.getRequiredTestInstance());
        }
    }


    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return false;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return null;
    }

    private String canonicalName(String name) {
        return name.toLowerCase().replaceAll("[^0-9a-z]+", "");
    }

    private CircuitBoard lookupSubcircuit(CircuitSim circuitSim, String subcircuitName) {
        String canonicalSubcircuitName = canonicalName(subcircuitName);
        Map<String, CircuitBoard> boards = circuitSim.getCircuitBoards();
        List<CircuitBoard> matchingCircuits = boards.entrySet().stream()
            .filter(entry -> canonicalName(entry.getKey()).equals(canonicalSubcircuitName))
            .map(Map.Entry::getValue)
            .collect(Collectors.toList());

        if (matchingCircuits.size() == 0) {
            throw new IllegalArgumentException(String.format(
                "No subcircuits match the name `%s'." +
                "Please double-check the names of all your subcircuits.",
                subcircuitName));
        } else if (matchingCircuits.size() > 1) {
            throw new IllegalArgumentException(String.format(
                "More than one subcircuit has the name `%s'." +
                "Can't continue deterministically.",
                subcircuitName));
        }

        return matchingCircuits.get(0);
    }

    private List<FieldInjection> lookupPins(CircuitBoard circuitBoard, Class<?> testClass) {
        List<FieldInjection> fieldInjections = new LinkedList<>();

        List<Field> pinFields = Arrays.stream(testClass.getDeclaredFields())
                                      .filter(field -> field.isAnnotationPresent(SubcircuitPin.class))
                                      .collect(Collectors.toList());

        // TODO: Detect duplicate pins (`Pin a' and `Pin b' are distinct
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
            String canonicalPinLabel = canonicalName(pinAnnotation.value().isEmpty()? pinField.getName()
                                                                                    : pinAnnotation.value());

            // TODO: Search circuit for the pins we want
            List<PinPeer> matchingPins = circuitBoard.getComponents().stream()
                .filter(component -> component instanceof PinPeer &&
                        ((PinPeer) component).getProperties().containsProperty(Properties.LABEL) &&
                        canonicalPinLabel.equals(
                            canonicalName(((PinPeer) component).getProperties().getValue(Properties.LABEL))))
                .map(component -> (PinPeer) component)
                .collect(Collectors.toList());

            if (matchingPins.size() > 1) {
                throw new IllegalArgumentException(String.format(
                    "Subcircuit `%s' contains %d input/output pins labelled `%s', expected 1",
                    circuitBoard.getCircuit().getName(), matchingPins.size(), canonicalPinLabel));
            } else if (matchingPins.isEmpty()) {
                throw new IllegalArgumentException(String.format(
                    "Subcircuit `%s' contains no input/output pins labelled `%s'!",
                    circuitBoard.getCircuit().getName(), canonicalPinLabel));
            }

            PinPeer matchingPin = matchingPins.get(0);

            if (matchingPin.isInput() != wantInputPin) {
                throw new IllegalArgumentException(String.format(
                    "Subcircuit `%s' has %s pin labelled `%s', but expected it to be an %s pin instead",
                    circuitBoard.getCircuit().getName(),
                    // Use their label to be less confusing
                    matchingPin.getProperties().getValue(Properties.LABEL),
                    matchingPin.isInput()? "input" : "output",
                    wantInputPin? "input" : "output"));
            }

            BasePin pinWrapper = wantInputPin? new InputPin(matchingPin.getComponent())
                                             : new OutputPin(matchingPin.getComponent());

            fieldInjections.add(new FieldInjection(pinField, pinWrapper));
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
