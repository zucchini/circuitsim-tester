package io.zucchini.circuitsimtester.extension;

import org.junit.jupiter.params.converter.SimpleArgumentConverter;

/**
 * Converts String arguments into integers using the base corresponding to their prefix.
 * Uses:
 * <ul>
 * <li>{@code 0b} for binary (like {@code 0b010010})</li>
 * <li>{@code 0x} for hex (like {@code 0xDEADBEEF})</li>
 * <li>none for decimal (like {@code 420})</li>
 * </ul>
 * <p>
 * Looks prettier than cluttering your tests with parseInt calls everywhere.
 * <p>
 * Example:
 * <pre>
 * {@literal @}ParameterizedTest(name="a:{0}, b:{1}, sel:00 (a xor b) → out:{2}")
 * {@literal @}CsvSource({
 *     /*  a      b    |  out *{@literal /}
 *     "0b1111, 0b0000, 0b1111",
 *     "0b0000, 0b1111, 0b1111",
 *     "0b1111, 0b1111, 0b0000",
 *     "0b1011, 0b0010, 0b1001",
 * })
 * public void xor({@literal @}ConvertWith(BasesConverter.class) int aIn,
 *                 {@literal @}ConvertWith(BasesConverter.class) int bIn,
 *                 {@literal @}ConvertWith(BasesConverter.class) int outOut) {
 *     a.set(aIn);
 *     b.set(bIn);
 *     sel.set(0b00);
 *     assertEquals(outOut, out.get(), "out");
 * }
 * </pre>
 *
 * @see <a href="https://github.com/zucchini/circuitsim-tester/blob/master/README.md">The README with examples</a>
 */
public class BasesConverter extends SimpleArgumentConverter {
    @Override
    protected Object convert(Object source, Class<?> targetType) {
        if (!(source instanceof String) || targetType != Integer.TYPE) {
            throw new IllegalArgumentException("BaseConverter must go from String to int");
        }

        String str = ((String) source).toLowerCase();

        int base;
        if (str.startsWith("0b")) {
            base = 2;
            str = str.substring(2);
        } else if (str.startsWith("0x")) {
            base = 16;
            str = str.substring(2);
        } else {
            base = 10;
        }

        return Integer.parseInt(str, base);
    }
}
