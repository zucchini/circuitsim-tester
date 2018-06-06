package edu.gatech.cs2110.circuitsim.extensions;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

@Retention(RUNTIME)
public @interface SubcircuitPin {
    String value() default ""; // name of pin (default: name of variable)
}
