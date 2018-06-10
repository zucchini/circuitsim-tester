package edu.gatech.cs2110.circuitsim.launcher;

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

    public void printResultsAsJson(TestClassResult classResult, Appendable out) {
        ZucchiniJsonRoot root;
        boolean success = classResult.getResult().getStatus() == SUCCESSFUL;

        // Zucchini treats an error as a 0, so don't bother writing test
        // results unless there were no errors.
        if (success) {
            root = new ZucchiniJsonRoot(
                classResult.getMethodResults().stream()
                                              .map(ZucchiniJsonMethod::fromMethodResult)
                                              .collect(Collectors.toList()));
        } else {
            root = new ZucchiniJsonRoot(
                classResult.getResult().getThrowable().get().getMessage());
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
        private String methodName;
        private boolean passed;
        private String message;

        public ZucchiniJsonMethod(String displayName, String methodName,
                                  boolean passed, String message) {
            this.displayName = displayName;
            this.methodName = methodName;
            this.passed = passed;
            this.message = message;
        }

        public static ZucchiniJsonMethod fromMethodResult(TestMethodResult methodResult) {
            boolean passed = methodResult.getResult().getStatus() == SUCCESSFUL;
            String message = methodResult.getResult().getThrowable().map(err -> err.getMessage())
                                                                    .orElse(null);
            return new ZucchiniJsonMethod(methodResult.getId().getDisplayName(),
                                          methodResult.getSource().getMethodName(),
                                          passed, message);
        }
    }
}
