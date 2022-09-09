package io.zucchini.circuitsimtester.api;

import java.io.InputStream;
import java.util.Scanner;

/**
 * Base class inherited by {@link Ram} and {@link Rom}. Handles loading {@code
 * .dat}s exported from CircuitSim from an {@link InputStream}.
 */
public abstract class BaseMemory {
    /**
     * Reads a word from this RAM/ROM.
     *
     * @param address The memory address to load from
     * @return value at this address
     */
    public abstract int load(int address);

    /**
     * Writes a word to this RAM/ROM.
     *
     * @param address The memory address to store to
     * @param value The value to store
     */
    public abstract void store(int address, int value);

    // TODO: This method is badly named, so rename it. After all, we are
    //       storing into RAM/ROM, not loading from it.
    // TODO: Provide some mechanism in CircuitSimExtension to open a file
    //       and preload it into RAM/ROM
    /**
     * Loads a stream of a dat file into this component's memory. Format
     * is the same as the .dat files saved in the CircuitSim memory
     * editor window.
     *
     * @param stream the stream of the .dat file to be loaded
     */
    public void load(InputStream stream) {
        Scanner scanner = new Scanner(stream);

        int address = 0;
        while (scanner.hasNext()) {
            String word = scanner.next();

            if (word.contains("-")) {
                // Roi's world-famous patented run-length encoding
                // `x-y', where x is the number of occurrences and y is a
                // hex word
                String[] pieces = word.split("-");
                if (pieces.length != 2) {
                    throw new IllegalArgumentException("invalid run-length encoded word " + word);
                }
                int length = Integer.parseInt(pieces[0]);
                int value = Integer.parseInt(pieces[1], 16);

                for (int i = 0; i < length; i++) {
                    store(address++, value);
                }
            } else {
                int value = Integer.parseInt(word, 16);
                store(address++, value);
            }
        }
    }
}
