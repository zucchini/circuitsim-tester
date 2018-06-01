package edu.gatech.cs2110.circuitsim;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;

public class TesterLauncher {
    public static void main(String[] args) {
        System.exit(new TesterLauncher().execute());
    }

    private TesterLauncher() {}

    private int execute() {
        Launcher launcher = LauncherFactory.create();
        launcher.execute(buildDiscoveryRequest(), new TestListener());
        return 0;
    }

    private LauncherDiscoveryRequest buildDiscoveryRequest() {
        LauncherDiscoveryRequestBuilder builder = new LauncherDiscoveryRequestBuilder();
        builder.selectors(selectPackage("edu.gatech.cs2110.circuitsim.tests"));
        return builder.build();
    }

    private class TestListener implements TestExecutionListener {
        public void executionFinished(TestIdentifier testIdentifier,
                                      TestExecutionResult testExecutionResult) {
            System.err.printf("test %s: %s\n", testIdentifier.getDisplayName(),
                                               testExecutionResult.getStatus());
        }
    }
}
