package edu.gatech.cs2110.circuitsim.api;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

/**
 * Marks this JUnit test class as testing a subcircuit with the provided
 * {@code .sim} file path and subcircuit name.
 *
 * @see Subcircuit#fromPath(String,String)
 */
@Retention(RUNTIME)
public @interface SubcircuitTest {
    /**
     * The circuit file to open. Usually a relative path.
     *
     * @return the path of a {@code .sim} file
     * @see Subcircuit#fromPath(String,String)
     */
    String file();

    /**
     * The name of the subcircuit in which to search for pins.
     * Normalized as described in {@link Subcircuit#fromPath(String,String)}.
     *
     * @return the name of the subcircuit. Normalized before lookup
     * @see Subcircuit#fromPath(String,String)
     */
    String subcircuit();
}
