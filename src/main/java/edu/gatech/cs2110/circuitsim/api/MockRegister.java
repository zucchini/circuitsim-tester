package edu.gatech.cs2110.circuitsim.api;

public class MockRegister {
    private InputPin q;
    private OutputPin d;
    private OutputPin en;
    private OutputPin clk;
    private OutputPin rst;
    private Subcircuit subcircuit;

    MockRegister(InputPin q, OutputPin d, OutputPin en, OutputPin clk,
                 OutputPin rst, Subcircuit subcircuit) {
        this.q = q;
        this.d = d;
        this.en = en;
        this.clk = clk;
        this.rst = rst;
        this.subcircuit = subcircuit;
    }

    public InputPin getQ() {
        return q;
    }

    public OutputPin getD() {
        return d;
    }

    public OutputPin getEn() {
        return en;
    }

    public OutputPin getClk() {
        return clk;
    }

    public OutputPin getRst() {
        return rst;
    }

    public Subcircuit getSubcircuit() {
        return subcircuit;
    }
}
