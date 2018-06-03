package edu.gatech.cs2110.circuitsim.extensions;

import java.io.File;

import com.ra4king.circuitsim.gui.CircuitSim;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

public class CircuitSimExtension implements Extension, BeforeAllCallback, ParameterResolver {
    private CircuitSim circuitSim;

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
        if (circuitFile.canRead()) {
            throw new IllegalArgumentException(
                String.format("Cannot read circuit file %s", subcircuitAnnotation.file()));
        }

        circuitSim = new CircuitSim(false);
        circuitSim.loadCircuits(circuitFile);
    }

    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return false;
    }

    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return null;
    }
}
