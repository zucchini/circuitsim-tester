package edu.gatech.cs2110.circuitsim.launcher;

import static org.junit.platform.engine.TestExecutionResult.Status.SUCCESSFUL;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.launcher.TestIdentifier;

public class TestClassResult implements Comparable<TestClassResult> {
    private TestIdentifier id;
    private ClassSource source;
    private TestExecutionResult result;
    private SortedSet<TestMethodResult> methodResults;
    private int numFailed;

    public TestClassResult(TestIdentifier id,
                           ClassSource source,
                           TestExecutionResult result) {
        this.id = id;
        this.source = source;
        this.result = result;
        this.methodResults = new TreeSet<>();
        this.numFailed = 0;
    }

    public TestIdentifier getId() { return id; }
    public ClassSource getSource() { return source; }
    public TestExecutionResult getResult() { return result; }
    public Collection<TestMethodResult> getMethodResults() { return methodResults; }

    @Override
    public int compareTo(TestClassResult other) {
        return source.getClassName().compareTo(other.source.getClassName());
    }

    public boolean addMethodResult(TestMethodResult methodResult) {
        if (methodResult.getResult().getStatus() != SUCCESSFUL) {
            numFailed++;
        }

        return methodResults.add(methodResult);
    }

    public int getNumFailed() {
        return numFailed;
    }
}
