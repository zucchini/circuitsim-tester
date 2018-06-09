package edu.gatech.cs2110.circuitsim.api;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.ra4king.circuitsim.gui.CircuitBoard;
import com.ra4king.circuitsim.gui.CircuitSim;
import com.ra4king.circuitsim.gui.peers.memory.RegisterPeer;
import com.ra4king.circuitsim.gui.peers.wiring.PinPeer;
import com.ra4king.circuitsim.gui.peers.wiring.PinPeer;
import com.ra4king.circuitsim.gui.Properties;
import com.ra4king.circuitsim.gui.Properties;
import com.ra4king.circuitsim.simulator.Circuit;
import com.ra4king.circuitsim.simulator.CircuitState;
import com.ra4king.circuitsim.simulator.components.memory.Register;
import com.ra4king.circuitsim.simulator.components.wiring.Pin;
import com.ra4king.circuitsim.simulator.Port;
import com.ra4king.circuitsim.simulator.Simulator;

public class Subcircuit {
    private CircuitSim circuitSim;
    private CircuitBoard circuitBoard;

    public Subcircuit(CircuitSim circuitSim, CircuitBoard circuitBoard) {
        this.circuitSim = circuitSim;
        this.circuitBoard = circuitBoard;
    }

    public CircuitSim getCircuitSim() {
        return circuitSim;
    }

    public Simulator getSimulator() {
        return getCircuitSim().getSimulator();
    }

    public CircuitBoard getCircuitBoard() {
        return circuitBoard;
    }

    public Circuit getCircuit() {
        return circuitBoard.getCircuit();
    }

    public CircuitState getCircuitState() {
        return getCircuit().getTopLevelState();
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

    public BasePin lookupPin(String pinLabel, boolean wantInputPin, int wantBits) {
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
        // Use their labels for error messages to be less confusing
        String theirPinLabel = matchingPin.getProperties().getValue(Properties.LABEL);

        if (matchingPin.isInput() != wantInputPin) {
            throw new IllegalArgumentException(String.format(
                "Subcircuit `%s' has %s pin labelled `%s', but expected it to be an %s pin instead",
                circuitBoard.getCircuit().getName(),
                theirPinLabel,
                matchingPin.isInput()? "input" : "output",
                wantInputPin? "input" : "output"));
        }

        int actualBits = matchingPin.getComponent().getBitSize();
        if (actualBits != wantBits) {
            throw new IllegalArgumentException(String.format(
                "Subcircuit `%s' has pin labelled `%s' with %d bits, but expected %d bits",
                circuitBoard.getCircuit().getName(),
                theirPinLabel, actualBits, wantBits));
        }

        BasePin pinWrapper = wantInputPin? new InputPin(matchingPin.getComponent(), this)
                                         : new OutputPin(matchingPin.getComponent(), this);
        return pinWrapper;
    }

    public MockRegister mockRegister(int wantBits) {
        List<Register> registers = circuitBoard.getComponents().stream()
            .filter(component -> component instanceof RegisterPeer)
            .map(component -> ((RegisterPeer) component).getComponent())
            .collect(Collectors.toList());

        if (registers.size() > 1) {
            throw new IllegalArgumentException(String.format(
                "Subcircuit `%s' contains %d registers, expected 1",
                circuitBoard.getCircuit().getName(), registers.size()));
        } else if (registers.isEmpty()) {
            throw new IllegalArgumentException(String.format(
                "Subcircuit `%s' contains no registers!",
                circuitBoard.getCircuit().getName()));
        }

        Register reg = registers.get(0);

        int actualBits = reg.getBitSize();
        if (actualBits != wantBits) {
            throw new IllegalArgumentException(String.format(
                "Subcircuit `%s' has register with %d bits, but expected %d bits",
                circuitBoard.getCircuit().getName(), actualBits, wantBits));
        }

        // Idea: We want to test a circuit that looks like this:
        //                ______
        //       .-------|D    Q|------.
        //       |       |      |      |
        //       |       |_/\___|      |
        //       |                     |
        //       '--[ combinational ]--'
        //          [    logic      ]
        //
        // But we want to test that combinational logic; who cares about
        // the register. So rewire the circuit to look like this
        // instead, where d is an output pin, and q is an input pin:
        //
        //       .-------(d)  [q]------.
        //       |                     |
        //       |                     |
        //       |                     |
        //       '--[ combinational ]--'
        //          [    logic      ]
        //
        // This way, we can test that combinational logic on its own by
        // checking the value of d and setting the value of q.

        InputPin q = new InputPin(substitutePin(reg.getPort(Register.PORT_OUT), true), this);
        OutputPin d = new OutputPin(substitutePin(reg.getPort(Register.PORT_IN), false), this);
        OutputPin en = new OutputPin(substitutePin(reg.getPort(Register.PORT_ENABLE), false), this);
        OutputPin clk = new OutputPin(substitutePin(reg.getPort(Register.PORT_CLK), false), this);
        OutputPin rst = new OutputPin(substitutePin(reg.getPort(Register.PORT_ZERO), false), this);

        return new MockRegister(q, d, en, clk, rst, this);
    }

    public Collection<Port> makeOrphanPort(Port port) {
        Collection<Port> connectedPorts = port.getLink().getParticipants().stream()
                                              .filter(p -> p != port).collect(Collectors.toList());
        connectedPorts.stream().forEach(port::unlinkPort);
        return connectedPorts;
    }

    public Pin substitutePin(Port port, boolean isInput) {
        Collection<Port> originallyConnectedPorts = makeOrphanPort(port);

        PinPeer mockPinPeer = new PinPeer(new Properties(
            new Properties.Property<>(Properties.BITSIZE, port.getLink().getBitSize()),
            new Properties.Property<>(PinPeer.IS_INPUT, isInput)
        ), 0, 0);

        // Find a valid place to drop it
        while (!circuitBoard.isValidLocation(mockPinPeer)) {
            mockPinPeer.setX(mockPinPeer.getX() + 32);
            mockPinPeer.setY(mockPinPeer.getY() + 32);
        }

        circuitBoard.addComponent(mockPinPeer);

        Pin mockPin = mockPinPeer.getComponent();
        Port newPort = mockPin.getPort(Pin.PORT);
        // In case CircuitSim automatically connected this new Pin to
        // anything, disconnect it
        makeOrphanPort(newPort);
        // Now reconnect all the old stuff
        originallyConnectedPorts.stream().forEach(newPort::linkPort);

        return mockPin;
    }
}
