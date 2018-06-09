package edu.gatech.cs2110.circuitsim.api;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

@Retention(RUNTIME)
public @interface SubcircuitTest {
    String file();
    String subcircuit();
}
