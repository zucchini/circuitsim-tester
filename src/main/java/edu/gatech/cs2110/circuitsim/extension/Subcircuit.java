package edu.gatech.cs2110.circuitsim.extension;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ra4king.circuitsim.gui.CircuitSim;
import com.ra4king.circuitsim.gui.CircuitBoard;
import com.ra4king.circuitsim.gui.Properties;
import com.ra4king.circuitsim.gui.peers.wiring.PinPeer;

class Subcircuit {
    private CircuitSim circuitSim;
    private CircuitBoard circuitBoard;

    public Subcircuit(CircuitSim circuitSim, CircuitBoard circuitBoard) {
        this.circuitSim = circuitSim;
        this.circuitBoard = circuitBoard;
    }

    // Don't hate me for `throws Exception', Roi made me do it
    public static Subcircuit fromPath(String simFilePath, String subcircuitName) throws Exception {
        File circuitFile = new File(simFilePath);

        // In theory, zucchini should guarantee this for us, but stuff
        // happens
        // TODO: Race condition R I P
        if (!circuitFile.canRead()) {
            throw new IllegalArgumentException(
                String.format("Cannot read circuit file `%s'", simFilePath));
        }

        CircuitSim circuitSim = new CircuitSim(false);
        circuitSim.loadCircuits(circuitFile);

        CircuitBoard circuitBoard = lookupSubcircuit(circuitSim, subcircuitName);

        return new Subcircuit(circuitSim, circuitBoard);
    }

    private static String canonicalName(String name) {
        return name.toLowerCase().replaceAll("[^0-9a-z]+", "");
    }

    private static CircuitBoard lookupSubcircuit(CircuitSim circuitSim, String subcircuitName) {
        String canonicalSubcircuitName = canonicalName(subcircuitName);
        Map<String, CircuitBoard> boards = circuitSim.getCircuitBoards();
        List<CircuitBoard> matchingCircuits = boards.entrySet().stream()
            .filter(entry -> canonicalName(entry.getKey()).equals(canonicalSubcircuitName))
            .map(Map.Entry::getValue)
            .collect(Collectors.toList());

        if (matchingCircuits.size() == 0) {
            throw new IllegalArgumentException(String.format(
                "No subcircuits match the name `%s'." +
                "Please double-check the names of all your subcircuits.",
                subcircuitName));
        } else if (matchingCircuits.size() > 1) {
            throw new IllegalArgumentException(String.format(
                "More than one subcircuit has the name `%s'." +
                "Can't continue deterministically.",
                subcircuitName));
        }

        return matchingCircuits.get(0);
    }

    public void resetSimulation() {
        circuitSim.getSimulator().reset();
    }

    public BasePin lookupPin(String pinLabel, boolean wantInputPin) {
        String canonicalPinLabel = canonicalName(pinLabel);
        List<PinPeer> matchingPins = circuitBoard.getComponents().stream()
            .filter(component -> component instanceof PinPeer &&
                    ((PinPeer) component).getProperties().containsProperty(Properties.LABEL) &&
                    canonicalPinLabel.equals(
                        canonicalName(((PinPeer) component).getProperties().getValue(Properties.LABEL))))
            .map(component -> (PinPeer) component)
            .collect(Collectors.toList());

        if (matchingPins.size() > 1) {
            throw new IllegalArgumentException(String.format(
                "Subcircuit `%s' contains %d input/output pins labelled `%s', expected 1",
                circuitBoard.getCircuit().getName(), matchingPins.size(), canonicalPinLabel));
        } else if (matchingPins.isEmpty()) {
            throw new IllegalArgumentException(String.format(
                "Subcircuit `%s' contains no input/output pins labelled `%s'!",
                circuitBoard.getCircuit().getName(), canonicalPinLabel));
        }

        PinPeer matchingPin = matchingPins.get(0);

        if (matchingPin.isInput() != wantInputPin) {
            throw new IllegalArgumentException(String.format(
                "Subcircuit `%s' has %s pin labelled `%s', but expected it to be an %s pin instead",
                circuitBoard.getCircuit().getName(),
                // Use their label to be less confusing
                matchingPin.getProperties().getValue(Properties.LABEL),
                matchingPin.isInput()? "input" : "output",
                wantInputPin? "input" : "output"));
        }

        BasePin pinWrapper = wantInputPin? new InputPin(matchingPin.getComponent(), this)
                                         : new OutputPin(matchingPin.getComponent(), this);
        return pinWrapper;
    }
}
