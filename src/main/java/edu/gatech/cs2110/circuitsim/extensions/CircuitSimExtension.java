package edu.gatech.cs2110.circuitsim.extensions;

import java.io.File;

import com.ra4king.circuitsim.gui.CircuitSim;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

public class CircuitSimExtension implements Extension, BeforeAllCallback, BeforeEachCallback,
                                            ParameterResolver {
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
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        // Reset simulator before each test
        circuitSim.getSimulator().reset();
    }


    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return false;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return null;
    }
}
