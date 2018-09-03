package edu.gatech.cs2110.circuitsim.launcher;

import static org.junit.platform.engine.TestExecutionResult.Status.SUCCESSFUL;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ZucchiniJson {
    private int maxFailuresPerTest;
    private Gson gson;

    public ZucchiniJson(int maxFailuresPerTest) {
        this.maxFailuresPerTest = maxFailuresPerTest;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void printResultsAsJson(TestClassResult classResult, Appendable out) {
        ZucchiniJsonRoot root;
        boolean success = classResult.getResult().getStatus() == SUCCESSFUL;

        // Zucchini treats an error as a 0, so don't bother writing test
        // results unless there were no errors.
        if (success) {
            root = new ZucchiniJsonRoot(collapseMethodResults(
                classResult.getMethodResults()));
        } else {
            root = new ZucchiniJsonRoot(
                classResult.getResult().getThrowable().get().getMessage());
        }

        gson.toJson(root, out);
    }

    // Assumes results is sorted by method name
    private List<ZucchiniJsonMethod> collapseMethodResults(
            Collection<TestMethodResult> results) {
        List<ZucchiniJsonMethod> collapsed = new LinkedList<>();

        for (TestMethodResult result : results) {
            String methodName = result.getSource().getMethodName();
            ZucchiniJsonMethod tail;
            if (collapsed.isEmpty() ||
                    !(tail = collapsed.get(collapsed.size() - 1)).methodName
                                                                 .equals(methodName)) {
                // Time to start a new methodresult
                collapsed.add(tail = new ZucchiniJsonMethod(methodName));
            }

            tail.total++;
            if (result.getResult().getStatus() != SUCCESSFUL &&
                    ++tail.failed <= maxFailuresPerTest) {
                tail.partialFailures.add(
                    ZucchiniJsonMethodFailure.fromMethodResult(result));
            }
        }

        return collapsed;
    }

    private static class ZucchiniJsonRoot {
        private String error;
        private List<ZucchiniJsonMethod> tests;

        public ZucchiniJsonRoot() {
            this.tests = new LinkedList<>();
        }

        public ZucchiniJsonRoot(String error) {
            this.error = error;
        }

        public ZucchiniJsonRoot(List<ZucchiniJsonMethod> tests) {
            this.tests = tests;
        }
    }

    private static class ZucchiniJsonMethod {
        private String methodName;
        private int failed;
        private int total;
        private List<ZucchiniJsonMethodFailure> partialFailures;

        public ZucchiniJsonMethod(String methodName) {
            this.methodName = methodName;
            this.failed = 0;
            this.total = 0;
            this.partialFailures = new LinkedList<>();
        }
    }

    private static class ZucchiniJsonMethodFailure {
        private String displayName;
        private String message;

        public ZucchiniJsonMethodFailure(String displayName, String message) {
            this.displayName = displayName;
            this.message = message;
        }

        public static ZucchiniJsonMethodFailure fromMethodResult(TestMethodResult result) {
            String displayName = result.getId().getDisplayName();
            String message = result.getResult().getThrowable().map(err -> err.getMessage())
                                                              .orElse(null);
            return new ZucchiniJsonMethodFailure(displayName, message);
        }
    }
}
