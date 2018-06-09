CircuitSim Autograder Template
==============================

This repository aims to bridge the gap between the nitty-gritty details
of the [CircuitSim][1] API and high-level tests on combinational logic
in a CircuitSim file. Until now, we (the CS 2110 staff) handled
CircuitSim homeworks with one of the following strategies:

 1. Don't provide a tester because they were scary
 2. Copy and paste some old tests, often missing error handling
    because the tons of duplicate code and intertwined CircuitSim
    implementation details made the tests hard to follow

So I have two goals in this template:

 1. Make CircuitSim autograders easy to write, for students too if they
    want
 2. Insulate tests from the verboseness of the CircuitSim API by
    wrapping the CircuitSim API, and pass these wrappers to test classes
    using dependency injection

TODO: explain what this is

[1]: https://github.com/ra4king/CircuitSim
