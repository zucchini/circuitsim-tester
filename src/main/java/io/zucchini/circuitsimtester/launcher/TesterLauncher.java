package io.zucchini.circuitsimtester.launcher;

import static org.junit.platform.engine.TestExecutionResult.Status.SUCCESSFUL;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

import java.io.PrintStream;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;

public class TesterLauncher {
    private static final int MAX_FAILURES = 8;
    private String pkg;
    private SortedSet<TestClassResult> results;
    private PrintStream out, err;

    public static void launch(String pkg, String[] args) {
        boolean verbose = false;
        boolean student = args.length <= 1 && (args.length == 0 || (verbose = args[0].equals("--verbose")));
        boolean zucchini = args.length == 2 && args[0].equals("--zucchini");

        if (!student && !zucchini) {
            System.err.println("usage: java -jar tester.jar [--verbose]");
            System.err.println("           → run student tests (optionally, with verbose output)");
            System.err.println("       java -jar tester.jar --zucchini SomeTestClass");
            System.err.println("           → run and generate zucchini json for test SomeTestClass");
            System.exit(1);
            return;
        }

        if (student) {
            System.exit(studentRun(pkg, verbose));
        } else { // zucchini
            String testClassName = args[1];
            System.exit(zucchiniRun(pkg, testClassName));
        }
    }

    private static int studentRun(String testPackage, boolean verbose) {
        TesterLauncher launcher = new TesterLauncher(
                testPackage, System.out, System.err);
        launcher.runAllTests();
        launcher.printStudentSummary(verbose);
        return launcher.wasSuccessful()? 0 : 1;
    }

    private static int zucchiniRun(String testPackage, String testClassName) {
        TesterLauncher launcher = new TesterLauncher(
                testPackage, System.out, System.err);
        launcher.runTests(testClassName);
        launcher.printZucchiniJsonSummary();

        // Don't confuse zucchini backend by returning nonzero exit
        // code, even if some tests fail
        return 0;
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

    public void printStudentSummary(boolean verbose) {
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

                int numFailedPrinted = 0;

                for (TestMethodResult methodResult : classResult.getMethodResults()) {
                    if (methodResult.getResult().getStatus() != SUCCESSFUL) {
                        if (!printedSuite) {
                            out.printf("%nTest Suite: %s:%n", classResult.getId().getDisplayName());
                            printedSuite = true;
                        }

                        if (verbose) {
                            err.printf("[verbose] stack trace for failure:\n");
                            methodResult.getResult().getThrowable().get().printStackTrace(err);
                        }

                        out.printf("\t[FAIL] %s: %s%n", methodResult.getId().getDisplayName(),
                                   methodResult.getResult().getThrowable().get().getMessage());

                        if (++numFailedPrinted == MAX_FAILURES &&
                                classResult.getNumFailed() > numFailedPrinted) {
                            out.printf("\t[%d more failures omitted]%n",
                                       classResult.getNumFailed() - numFailedPrinted);
                            break;
                        }
                    }
                }
            }
        }
    }

    public void printZucchiniJsonSummary() {
        // Only ran one class so should be just one TestClassResult
        TestClassResult classResult = results.stream().findFirst().get();
        new ZucchiniJson(MAX_FAILURES).printResultsAsJson(classResult, out);
    }

    private boolean wasSuccessful() {
        for (TestClassResult classResult : results) {
            if (classResult.getResult().getStatus() != SUCCESSFUL) {
                return false;
            }

            if (classResult.getNumFailed() > 0) {
                return false;
            }
        }

        return true;
    }

    private LauncherDiscoveryRequest buildDiscoveryRequest(String testClassName) {
        LauncherDiscoveryRequestBuilder builder = new LauncherDiscoveryRequestBuilder();

        if (testClassName == null) {
            builder.selectors(selectPackage(pkg));
        } else {
            String fullyQualifiedClassName = String.format("%s.%s", pkg, testClassName);
            builder.selectors(selectClass(fullyQualifiedClassName));
        }

        return builder.build();
    }
}
