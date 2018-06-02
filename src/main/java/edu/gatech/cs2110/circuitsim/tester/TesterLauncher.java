package edu.gatech.cs2110.circuitsim.tester;

import static org.junit.platform.engine.TestExecutionResult.Status.SUCCESSFUL;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;

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

                for (TestMethodResult methodResult : classResult.getMethodResults()) {
                    if (methodResult.getResult().getStatus() != SUCCESSFUL) {
                        if (!printedSuite) {
                            out.printf("Test Suite: %s:%n", classResult.getId().getDisplayName());
                            printedSuite = true;
                        }
                        out.printf("\t[FAIL] %s: %s%n", methodResult.getId().getDisplayName(),
                                   methodResult.getResult().getThrowable().get().getMessage());
                    }
                }

                out.println();
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

    private LauncherDiscoveryRequest buildDiscoveryRequest() {
        LauncherDiscoveryRequestBuilder builder = new LauncherDiscoveryRequestBuilder();
        builder.selectors(selectPackage(pkg));
        return builder.build();
    }

}
