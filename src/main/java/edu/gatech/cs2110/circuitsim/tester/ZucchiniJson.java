package edu.gatech.cs2110.circuitsim.tester;

import static org.junit.platform.engine.TestExecutionResult.Status.SUCCESSFUL;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ZucchiniJson {
    private Gson gson;

    public ZucchiniJson() {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void printResultsAsJson(Collection<TestClassResult> results, Appendable out) {
        ZucchiniJsonRoot root;

        // Join all class error messages into a newline-separated
        // string
        String error = results.stream()
                              .filter(classResult -> classResult.getResult().getStatus() != SUCCESSFUL)
                              .map(classResult -> classResult.getResult().getThrowable().get().getMessage())
                              .collect(Collectors.joining("\n"));

        // Zucchini treats an error as a 0, so don't bother writing test
        // results unless there were no errors.
        if (error.isEmpty()) {
            root = new ZucchiniJsonRoot(
                results.stream().flatMap(classResult -> classResult.getMethodResults().stream()
                       .map(ZucchiniJsonMethod::fromMethodResult)).collect(Collectors.toList()));
        } else {
            root = new ZucchiniJsonRoot(error);
        }

        gson.toJson(root, out);
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
        private String displayName;
        private String fullMethodName;
        private boolean passed;
        private String message;

        public ZucchiniJsonMethod(String displayName, String fullMethodName,
                                  boolean passed, String message) {
            this.displayName = displayName;
            this.fullMethodName = fullMethodName;
            this.passed = passed;
            this.message = message;
        }

        public static ZucchiniJsonMethod fromMethodResult(TestMethodResult methodResult) {
            String fullMethodName = String.format("%s.%s", methodResult.getSource().getClassName(),
                                                  methodResult.getSource().getMethodName());
            boolean passed = methodResult.getResult().getStatus() == SUCCESSFUL;
            String message = methodResult.getResult().getThrowable().map(err -> err.getMessage())
                                                                    .orElse(null);
            return new ZucchiniJsonMethod(methodResult.getId().getDisplayName(),
                                          fullMethodName, passed, message);
        }
    }
}
