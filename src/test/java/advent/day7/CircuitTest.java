package advent.day7;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CircuitTest {

	@Test
	public void rshift() {
		assertEquals(0, 1 >> 2);
	}

	@Test
	public void reference() {
		Circuit circuit = new Circuit();

		circuit.addGate("123 -> x");
		circuit.addGate("456 -> y");
		circuit.addGate("x AND y -> d");
		circuit.addGate("x OR y -> e");
		circuit.addGate("x LSHIFT 2 -> f");
		circuit.addGate("y RSHIFT 2 -> g");
		circuit.addGate("NOT x -> h");
		circuit.addGate("NOT y -> i");

		assertEquals(circuit.get("d"), 72);
		assertEquals(circuit.get("e"), 507);
		assertEquals(circuit.get("f"), 492);
		assertEquals(circuit.get("g"), 114);
		assertEquals(circuit.get("h"), 65412);
		assertEquals(circuit.get("i"), 65079);
		assertEquals(circuit.get("x"), 123);
		assertEquals(circuit.get("y"), 456);
	}

}
