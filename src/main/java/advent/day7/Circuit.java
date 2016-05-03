package advent.day7;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Circuit {

	private Map<String, Gate> gatesByOutput = new HashMap<>();

	private Map<String, Integer> memoizedValues = new HashMap<>();

	public int get(String wire) {
		return this.memoizedValues.computeIfAbsent(wire, w -> this.gatesByOutput.get(w).get(this));
	}

	public void addGate(String representation) {
		Gate gate = Gate.of(representation);
		this.gatesByOutput.put(gate.getTargetWire(), gate);
		this.memoizedValues.clear();
	}

	public static void main(String[] args) throws IOException {
		Circuit circuit = new Circuit();
		Files.lines(Paths.get("src/main/java/advent/day7/input.txt")) //
				.forEach(circuit::addGate);

		System.out.println(circuit.get("a"));
	}

}
