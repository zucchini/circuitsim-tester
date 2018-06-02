package edu.gatech.cs2110.circuitsim;

import static org.junit.platform.engine.TestExecutionResult.Status.SUCCESSFUL;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;

public class TesterLauncher {
    private static final String TEST_PACKAGE = "edu.gatech.cs2110.circuitsim.tests";
    private String pkg;
    private List<TestClassResult> results;
    private PrintStream out, err;

    public static void main(String[] args) {
        boolean student = args.length == 0;
        boolean zucchini = args.length == 2 && args[0].equals("--zucchini");

        if (!student && !zucchini) {
            System.err.println("usage: java -jar tester.jar                                  (run student tests)");
            System.err.println("       java -jar tester.jar --zucchini path/to/result.json   (create zucchini json)");
            System.exit(1);
            return;
        }

        if (student) {
            System.exit(studentRun());
        } else { // zucchini
            String jsonOutputFile = args[1];
            System.exit(zucchiniRun(jsonOutputFile));
        }
    }


    private static int studentRun() {
        TesterLauncher launcher = new TesterLauncher(
            TEST_PACKAGE, System.out, System.err);
        launcher.runTests();
        launcher.printStudentSummary();
        return launcher.wasSuccessful()? 0 : 1;
    }

    private static int zucchiniRun(String jsonOutputFile) {
        try (PrintStream jsonOutputStream = new PrintStream(jsonOutputFile)) {
            TesterLauncher launcher = new TesterLauncher(
                TEST_PACKAGE, jsonOutputStream, System.err);
            launcher.runTests();
            launcher.printJsonSummary();

            // Don't confuse zucchini backend by returning nonzero exit
            // code, even if some tests fail
            return 0;
        } catch (FileNotFoundException err) {
            err.printStackTrace();
            return 1;
        }
    }

    private TesterLauncher(String pkg, PrintStream out, PrintStream err) {
        this.pkg = pkg;
        this.out = out;
        this.err = err;
    }

    private void runTests() {
        Launcher launcher = LauncherFactory.create();
        TestListener testListener = new TestListener();
        launcher.execute(buildDiscoveryRequest(), testListener);
        results = testListener.harvest();
    }

    public void printStudentSummary() {
        if (wasSuccessful()) {
            out.println("All student tests pass! Good job.");
            out.println();
            out.println("Note: As noted in the syllabus, we provide testers on a best-effort basis.");
            out.println("      You should make sure these tests pass, but we reserve the right to");
            out.println("      use a different tester for grading.");
        } else {
            out.println("Some student tests failed. Showing failed tests:");
            out.println();

            for (TestClassResult classResult : results) {
                boolean printedSuite = false;

                for (TestMethodResult methodResult : classResult.methodResults) {
                    if (methodResult.result.getStatus() != SUCCESSFUL) {
                        if (!printedSuite) {
                            out.printf("Test Suite: %s:%n", classResult.id.getDisplayName());
                            printedSuite = true;
                        }
                        out.printf("\t[FAIL] %s: %s%n", methodResult.id.getDisplayName(),
                                   methodResult.result.getThrowable().get().getMessage());
                    }
                }

                out.println();
            }
        }
    }

    public void printJsonSummary() {
        out.println("{\"FIX\": \"ME\"}");
    }

    private boolean wasSuccessful() {
        for (TestClassResult classResult : results) {
            for (TestMethodResult methodResult : classResult.methodResults) {
                if (methodResult.result.getStatus() != SUCCESSFUL) {
                    return false;
                }
            }
        }

        return true;
    }

    private LauncherDiscoveryRequest buildDiscoveryRequest() {
        LauncherDiscoveryRequestBuilder builder = new LauncherDiscoveryRequestBuilder();
        builder.selectors(selectPackage(pkg));
        return builder.build();
    }

    private class TestMethodResult implements Comparable<TestMethodResult> {
        private TestIdentifier id;
        private MethodSource source;
        private TestExecutionResult result;

        public TestMethodResult(TestIdentifier id,
                                MethodSource source,
                                TestExecutionResult result) {
            this.id = id;
            this.source = source;
            this.result = result;
        }

        @Override
        public int compareTo(TestMethodResult other) {
            return source.getMethodName().compareTo(other.source.getMethodName());
        }
    }

    private class TestClassResult implements Comparable<TestClassResult> {
        private TestIdentifier id;
        private ClassSource source;
        private TestExecutionResult result;
        private SortedSet<TestMethodResult> methodResults;

        public TestClassResult(TestIdentifier id,
                               ClassSource source,
                               TestExecutionResult result) {
            this.id = id;
            this.source = source;
            this.result = result;
            this.methodResults = new TreeSet<>();
        }

        @Override
        public int compareTo(TestClassResult other) {
            return source.getClassName().compareTo(other.source.getClassName());
        }

        public boolean addMethodResult(TestMethodResult methodResult) {
            return methodResults.add(methodResult);
        }
    }

    private class TestListener implements TestExecutionListener {
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
                err.printf("test %s has a source which is neither a class nor a method???\n", testIdentifier);
                return;
            }
        }

        // "Lord, thank you for this bountiful harvest" -Trevor Lusk
        public List<TestClassResult> harvest() {
            for (TestMethodResult methodResult : methodResults) {
                classResults.get(methodResult.source.getClassName())
                            .addMethodResult(methodResult);
            }

            return new LinkedList<>(classResults.values());
        }
    }
}
