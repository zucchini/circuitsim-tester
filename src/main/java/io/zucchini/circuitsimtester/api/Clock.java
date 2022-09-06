package io.zucchini.circuitsimtester.api;

import java.util.function.BooleanSupplier;

/**
 * Allows pretending to tick a Clock component placed in a subcircuit.
 * <p>
 * (In reality, this manipulates an InputPin wired to where the clock component was;
 * see {@link MockPulser})
 */
public class Clock extends MockPulser {
    public Clock(InputPin mockPin, Subcircuit subcircuit) {
        super(mockPin, subcircuit);
    }

    /**
     * Tick the clock once.
     */
    public void tick() {
        pulse();
    }

    /**
     * Tick the clock until {@code stopWhen} returns true.
     *
     * @param maxCycleCount the number of cycles before timeout (to avoid
     *                      infinitely spinning)
     * @param stopWhen returns true when we can stop ticking the clock (e.g.,
     *                 the student's processor has finished running and is
     *                 sending the "done" signal)
     * @return the number of ticks performed until stopWhen returned true. May
     *         be zero if the condition was initially met
     */
    public long tickUntil(long maxCycleCount, BooleanSupplier stopWhen) {
        long ticks = 0;
        while (!stopWhen.getAsBoolean()) {
            if (ticks > maxCycleCount) {
                throw new IllegalStateException(
                    "Ticked clock more than " + maxCycleCount + " times without finishing. " +
                    "Please check for errors in your logic");
            }
            tick();
            ticks++;
        }
        return ticks;
    }
}
