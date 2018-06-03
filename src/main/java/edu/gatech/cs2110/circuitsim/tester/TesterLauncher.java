package edu.gatech.cs2110.circuitsim.tester;

import static org.junit.platform.engine.TestExecutionResult.Status.SUCCESSFUL;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;

public class TesterLauncher {
    private static final String TEST_PACKAGE = "edu.gatech.cs2110.circuitsim.tests";
    private String pkg;
    private SortedSet<TestClassResult> results;
    private PrintStream out, err;

    public static void main(String[] args) {
        boolean student = args.length == 0;
        boolean zucchini = args.length >= 2 && args.length <= 3 && args[0].equals("--zucchini");

        if (!student && !zucchini) {
            System.err.println("usage: java -jar tester.jar");
            System.err.println("           → run student tests");
            System.err.println();
            System.err.println("       java -jar tester.jar --zucchini path/to/result.json");
            System.err.println("           → run generate zucchini json for all tests");
            System.err.println();
            System.err.printf ("       java -jar tester.jar --zucchini path/to/result.json %s.SomeTestClass%n", TEST_PACKAGE);
            System.err.println("           → run and generate zucchini json for test SomeTestClass");
            System.exit(1);
            return;
        }

        if (student) {
            System.exit(studentRun());
        } else { // zucchini
            String jsonOutputFile = args[1];
            String testClassName = (args.length >= 3)? args[2] : null;
            System.exit(zucchiniRun(jsonOutputFile, testClassName));
        }
    }

    private static int studentRun() {
        TesterLauncher launcher = new TesterLauncher(
            TEST_PACKAGE, System.out, System.err);
        launcher.runAllTests();
        launcher.printStudentSummary();
        return launcher.wasSuccessful()? 0 : 1;
    }

    private static int zucchiniRun(String jsonOutputFile, String testClassName) {
        try (PrintStream jsonOutputStream = new PrintStream(jsonOutputFile)) {
            TesterLauncher launcher = new TesterLauncher(
                TEST_PACKAGE, jsonOutputStream, System.err);
            launcher.runTests(testClassName);
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
        this.results = new TreeSet<>();
    }

    private void runAllTests() {
        runTests(null);
    }

    private void runTests(String testClassName) {
        Launcher launcher = LauncherFactory.create();
        TestListener testListener = new TestListener();
        launcher.execute(buildDiscoveryRequest(testClassName), testListener);
        results.addAll(testListener.harvest());
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

            for (TestClassResult classResult : results) {
                boolean printedSuite = false;

                if (classResult.getResult().getStatus() != SUCCESSFUL) {
                    out.printf("%n[FAIL] Test suite: %s: %s%n",
                               classResult.getId().getDisplayName(),
                               classResult.getResult().getThrowable().get().getMessage());
                    continue;
                }

                for (TestMethodResult methodResult : classResult.getMethodResults()) {
                    if (methodResult.getResult().getStatus() != SUCCESSFUL) {
                        if (!printedSuite) {
                            out.printf("%nTest Suite: %s:%n", classResult.getId().getDisplayName());
                            printedSuite = true;
                        }
                        out.printf("\t[FAIL] %s: %s%n", methodResult.getId().getDisplayName(),
                                   methodResult.getResult().getThrowable().get().getMessage());
                    }
                }
            }
        }
    }

    public void printJsonSummary() {
        new ZucchiniJson().printResultsAsJson(results, out);
    }

    private boolean wasSuccessful() {
        for (TestClassResult classResult : results) {
            for (TestMethodResult methodResult : classResult.getMethodResults()) {
                if (methodResult.getResult().getStatus() != SUCCESSFUL) {
                    return false;
                }
            }
        }

        return true;
    }

    private LauncherDiscoveryRequest buildDiscoveryRequest(String testClassName) {
        LauncherDiscoveryRequestBuilder builder = new LauncherDiscoveryRequestBuilder();

        if (testClassName == null) {
            builder.selectors(selectPackage(pkg));
        } else {
            builder.selectors(selectClass(testClassName));
        }

        return builder.build();
    }
}
