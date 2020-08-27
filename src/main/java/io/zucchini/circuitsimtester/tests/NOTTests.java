package io.zucchini.circuitsimtester.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.zucchini.circuitsimtester.api.InputPin;
import io.zucchini.circuitsimtester.api.OutputPin;
import io.zucchini.circuitsimtester.api.Restrictor;
import io.zucchini.circuitsimtester.api.Subcircuit;
import io.zucchini.circuitsimtester.api.SubcircuitComponent;
import io.zucchini.circuitsimtester.api.SubcircuitTest;
import io.zucchini.circuitsimtester.extension.CircuitSimExtension;

@DisplayName("NOT Gate")
@ExtendWith(CircuitSimExtension.class)
@SubcircuitTest(file="not.sim", subcircuit="not",
                restrictors={NOTTests.Restrictions.class})
public class NOTTests {
    public static class Restrictions extends Restrictor {
        @Override
        public void validate(Subcircuit subcircuit) throws AssertionError {
            whitelistComponents(subcircuit, "Transistor");

            // Check if they abused
            assertEquals(2, subcircuit.getPinCount(), "Total number of Pin components");
        }
    }

    @SubcircuitComponent(bits=1)
    private InputPin in;

    @SubcircuitComponent(bits=1)
    private OutputPin out;

    @ParameterizedTest(name="NOT in:{0} → out:{1}")
    @CsvSource({
     /* in | out */
        "0,  1",
        "1,  0",
    })
    public void xor(int inIn, int outOut) {
        in.set(inIn);
        assertEquals(outOut, out.get(), "out");
    }
}
