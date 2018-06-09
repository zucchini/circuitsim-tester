CircuitSim Autograder Template
==============================

Javadoc for API: <https://ausbin.github.io/circuitsim-grader-template/>

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

Getting Started
---------------

To make your own CircuitSim autograder:

 1. Clone this repository, probably replacing this README with your own
    and deleting the generated javadoc in `docs/`
 2. Edit the project name in `gradle.properties`
 3. Write some tests in `src/main/java/edu/gatech/cs2110/circuitsim/tests/`
 4. Test with `./gradlew run` and generate `build/libs/hwX-tester.jar`
    with `./gradlew jar`

### Writing Tests

#### Testing basic combinational logic

Suppose we had a Toy ALU like the following that we wanted to test:

![Screenshot of `toy-alu.sim`](https://i.imgur.com/tnemxwg.png)

You can find the full test at
`src/main/java/edu/gatech/cs2110/circuitsim/tests/ToyALU.java`, but
a brief walkthrough follows if you just want the highlights.

Let's start with the boilerplate test class:

```java
@DisplayName("Toy ALU")
@ExtendWith(CircuitSimExtension.class)
@SubcircuitTest(file="toy-alu.sim", subcircuit="ALU")
public class ToyALUTests {
    @SubcircuitPin(bits=4)
    private InputPin a;

    @SubcircuitPin(bits=4)
    private InputPin b;

    @SubcircuitPin(bits=2)
    private InputPin sel;

    @SubcircuitPin(bits=4)
    private OutputPin out;
}
```

`@DisplayName("Toy ALU")` lets students will see the name `Toy ALU` on
test failures (if you don't specify it, they will just see the ugly
fully-qualified class name instead), and
`@ExtendWith(CircuitSimExtension.class)` tells JUnit to run some custom
CircuitSim setup code first which loads up the subcircuit we want and
injects pins in it into the test class instance. Indeed,
`@SubcircuitTest(file="toy-alu.sim", subcircuit="ALU")` tells the
CircuitSim JUnit extension to load `toy-alu.sim` and then open its
subcircuit matching the name `ALU`. As described [in the API
documentation][2], this subcircuit name (as well as Pin labels later)
will be normalized into a lowercase alphanumeric string. That way,
`1-bit ALU` matches `1bit alu`, `1 bit ALU`, and so on.

The Pin fields are pretty self-explanatory: the extension looks up Pin
components in the subcircuit based on the name of fields annotated with
`@SubcircuitPin`. [If you want to match on a different label, say
`label="something else"`][3].

Let's start with testing the last operation, Pass A, since it's the easiest:

```java
    @DisplayName("pass A")
    @Test
    public void passA() {
        a.set(0);
        sel.set(0b11);
        assertEquals(0, out.get(), "out");

        a.set(0b1011);
        sel.set(0b11);
        assertEquals(0b1011, out.get(), "out");
    }
```

Well, that's pretty easy. But what if you need to test more than two
cases? That's where JUnit 5 parameterized tests come in:

```java
    @ParameterizedTest(name="a:{0}, b:{1}, sel:00 (a xor b) → out:{2}")
    @CsvSource({
        /*  a      b    |  out */
        "0b1111, 0b0000, 0b1111",
        "0b0000, 0b1111, 0b1111",
        "0b1111, 0b1111, 0b0000",
        "0b1011, 0b0010, 0b1001",
    })
    public void xor(@ConvertWith(BasesConverter.class) int aIn,
                    @ConvertWith(BasesConverter.class) int bIn,
                    @ConvertWith(BasesConverter.class) int outOut) {
        a.set(aIn);
        b.set(bIn);
        sel.set(0b00);
        assertEquals(outOut, out.get(), "out");
    }
```

JUnit will run this test once for every row in the CSV. Unlike a simple
for loop in a test, if one fails, it keeps going. The
`@ConvertWith(BasesConverter.class)` parses Strings to ints based on the
prefix. So `0x` is hex and `0b` is binary, and anything else is decimal.

You can also source your parameterized test from a method as follows:

```java
    @ParameterizedTest(name="a:{0}, b:{1}, sel:01 (a + b) → out:{2}")
    @MethodSource
    public void add(int aIn, int bIn,
                    int outOut) {
        a.set(aIn);
        b.set(bIn);
        sel.set(0b01);
        assertEquals(outOut, out.get(), "out");
    }

    public static Stream<Arguments> add() {
        List<Arguments> args = new LinkedList<>();

        for (int a = 0; a < (1 << 4); a++) {
            for (int b = 0; b < (1 << 4); b++) {
                args.add(Arguments.of(a, b, (a + b) % (1 << 4)));
            }
        }

        return args.stream();
    }
```

This allows for a much easier way to do an exhaustive test than a CSV!
Note that JUnit locates the second (**static**!) method because it has
the same name as the test.

You can see the finished product in
`src/main/java/edu/gatech/cs2110/circuitsim/tests/ToyALU.java`.

[1]: https://github.com/ra4king/CircuitSim
[2]: https://ausbin.github.io/circuitsim-grader-template/edu/gatech/cs2110/circuitsim/api/Subcircuit.html#fromPath(java.lang.String,java.lang.String)
[3]: https://ausbin.github.io/circuitsim-grader-template/edu/gatech/cs2110/circuitsim/api/SubcircuitPin.html
