package edu.gatech.cs2110.circuitsim.tester;

import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;

public class TestListener implements TestExecutionListener {
    private SortedMap<String, TestClassResult> classResults;
    private List<TestMethodResult> methodResults;

    public TestListener() {
        classResults = new TreeMap<>();
        methodResults = new LinkedList<>();
    }

    public void executionFinished(TestIdentifier testIdentifier,
                                  TestExecutionResult testExecutionResult) {
        if (!testIdentifier.getSource().isPresent()) {
            // The engine doesn't have a source, so this is probably
            // the engine node, so skip it
            return;
        }

        TestSource source = testIdentifier.getSource().get();
        if (source instanceof ClassSource) {
            ClassSource classSource = (ClassSource) source;
            TestClassResult tcr = new TestClassResult(
                testIdentifier, classSource, testExecutionResult);
            classResults.put(classSource.getClassName(), tcr);
        } else if (source instanceof MethodSource) {
            methodResults.add(new TestMethodResult(
                testIdentifier, (MethodSource) source, testExecutionResult));
        } else {
            throw new IllegalArgumentException(String.format(
                "test %s has a source which is neither a class nor a method",
                testIdentifier));
        }
    }

    // "Lord, thank you for this bountiful harvest" -Trevor Lusk
    public Collection<TestClassResult> harvest() {
        for (TestMethodResult methodResult : methodResults) {
            classResults.get(methodResult.getSource().getClassName())
                        .addMethodResult(methodResult);
        }

        methodResults.clear();

        return classResults.values();
    }
}
