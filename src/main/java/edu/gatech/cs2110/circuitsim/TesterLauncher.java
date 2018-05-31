package edu.gatech.cs2110.circuitsim;

import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.core.Launcher;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherDiscoveryRequest;

//import org.junit.platform.console.ConsoleLauncher;

public class TesterLauncher {
    public static void main(String[] args) {
        //ConsoleLauncher.main("--details", "none", "--select-package", "edu.gatech.cs2110.circuitsim.tests");
        System.exit(new TesterLauncher().execute());
    }

    private TesterLauncher() {}

    private int execute() {
        Launcher launcher = LauncherFactory.create();
        return 0;
    }

    private LauncherDiscoveryRequest buildDiscoveryRequest() {
        LauncherDiscoveryRequestBuilder builder = new LauncherDiscoveryRequestBuilder();
        return builder.build();
    }
}
