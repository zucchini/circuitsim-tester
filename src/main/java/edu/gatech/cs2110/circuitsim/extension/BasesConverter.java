package edu.gatech.cs2110.circuitsim.extension;

import org.junit.jupiter.params.converter.SimpleArgumentConverter;

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
