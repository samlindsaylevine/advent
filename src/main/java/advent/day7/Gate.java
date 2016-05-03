package advent.day7;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Gate {
	private final String targetWire;

	protected Gate(String targetWire) {
		this.targetWire = targetWire;
	}

	public String getTargetWire() {
		return this.targetWire;
	}

	public int get(Circuit circuit) {
		return this.getOutput(circuit) & 0xffff;
	}

	protected abstract int getOutput(Circuit circuit);

	static final Pattern SET_PATTERN = Pattern.compile("(\\d+) -> ([a-z]+)");
	static final Pattern SET_BY_WIRE_PATTERN = Pattern.compile("([a-z]+) -> ([a-z]+)");
	static final Pattern AND_PATTERN = Pattern.compile("([a-z]+) AND ([a-z]+) -> ([a-z]+)");
	static final Pattern NUMERIC_AND_PATTERN = Pattern.compile("(\\d+) AND ([a-z]+) -> ([a-z]+)");
	static final Pattern OR_PATTERN = Pattern.compile("([a-z]+) OR ([a-z]+) -> ([a-z]+)");
	static final Pattern LSHIFT_PATTERN = Pattern.compile("([a-z]+) LSHIFT (\\d+) -> ([a-z]+)");
	static final Pattern RSHIFT_PATTERN = Pattern.compile("([a-z]+) RSHIFT (\\d+) -> ([a-z]+)");
	static final Pattern NOT_PATTERN = Pattern.compile("NOT ([a-z]+) -> ([a-z]+)");

	public static Gate of(String instruction) {
		System.out.println(instruction);
		Matcher setMatcher = SET_PATTERN.matcher(instruction);
		if (setMatcher.matches()) {
			return new SetGate(Integer.valueOf(setMatcher.group(1)), setMatcher.group(2));
		}

		Matcher setByWireMatcher = SET_BY_WIRE_PATTERN.matcher(instruction);
		if (setByWireMatcher.matches()) {
			return new PipeGate(setByWireMatcher.group(1), setByWireMatcher.group(2));
		}

		Matcher andMatcher = AND_PATTERN.matcher(instruction);
		if (andMatcher.matches()) {
			return new AndGate(andMatcher.group(1), andMatcher.group(2), andMatcher.group(3));
		}

		Matcher numericAndMatcher = NUMERIC_AND_PATTERN.matcher(instruction);
		if (numericAndMatcher.matches()) {
			return new NumericAndGate(Integer.valueOf(numericAndMatcher.group(1)), numericAndMatcher.group(2),
					numericAndMatcher.group(3));
		}

		Matcher orMatcher = OR_PATTERN.matcher(instruction);
		if (orMatcher.matches()) {
			return new OrGate(orMatcher.group(1), orMatcher.group(2), orMatcher.group(3));
		}

		Matcher lshiftMatcher = LSHIFT_PATTERN.matcher(instruction);
		if (lshiftMatcher.matches()) {
			return new LshiftGate(lshiftMatcher.group(1), Integer.valueOf(lshiftMatcher.group(2)),
					lshiftMatcher.group(3));
		}

		Matcher rshiftMatcher = RSHIFT_PATTERN.matcher(instruction);
		if (rshiftMatcher.matches()) {
			return new RshiftGate(rshiftMatcher.group(1), Integer.valueOf(rshiftMatcher.group(2)),
					rshiftMatcher.group(3));
		}

		Matcher notMatcher = NOT_PATTERN.matcher(instruction);
		if (notMatcher.matches()) {
			return new NotGate(notMatcher.group(1), notMatcher.group(2));
		}

		throw new IllegalArgumentException(instruction);
	}

	private static class SetGate extends Gate {

		private final int value;

		public SetGate(int value, String wire) {
			super(wire);
			this.value = value;
		}

		@Override
		public int getOutput(Circuit circuit) {
			return this.value;
		}

	}

	private static class PipeGate extends Gate {
		private final String input;

		public PipeGate(String input, String wire) {
			super(wire);
			this.input = input;
		}

		@Override
		protected int getOutput(Circuit circuit) {
			return circuit.get(this.input);
		}
	}

	private static class AndGate extends Gate {
		private final String leftInput;
		private final String rightInput;

		private AndGate(String leftInput, String rightInput, String wire) {
			super(wire);
			this.leftInput = leftInput;
			this.rightInput = rightInput;
		}

		@Override
		protected int getOutput(Circuit circuit) {
			return circuit.get(this.leftInput) & circuit.get(this.rightInput);
		}

	}

	private static class NumericAndGate extends Gate {
		private final int amount;
		private final String input;

		private NumericAndGate(int amount, String input, String wire) {
			super(wire);
			this.amount = amount;
			this.input = input;
		}

		@Override
		protected int getOutput(Circuit circuit) {
			return circuit.get(this.input) & this.amount;
		}

	}

	private static class OrGate extends Gate {
		private final String leftInput;
		private final String rightInput;

		private OrGate(String leftInput, String rightInput, String wire) {
			super(wire);
			this.leftInput = leftInput;
			this.rightInput = rightInput;
		}

		@Override
		protected int getOutput(Circuit circuit) {
			return circuit.get(this.leftInput) | circuit.get(this.rightInput);
		}

	}

	private static class LshiftGate extends Gate {
		private final String input;
		private final int amount;

		private LshiftGate(String input, int amount, String wire) {
			super(wire);
			this.input = input;
			this.amount = amount;
		}

		@Override
		protected int getOutput(Circuit circuit) {
			return circuit.get(this.input) << this.amount;
		}
	}

	private static class RshiftGate extends Gate {
		private final String input;
		private final int amount;

		private RshiftGate(String input, int amount, String wire) {
			super(wire);
			this.input = input;
			this.amount = amount;
		}

		@Override
		protected int getOutput(Circuit circuit) {
			return circuit.get(this.input) >> this.amount;
		}
	}

	private static class NotGate extends Gate {
		private final String input;

		public NotGate(String input, String wire) {
			super(wire);
			this.input = input;
		}

		@Override
		protected int getOutput(Circuit circuit) {
			return ~circuit.get(this.input);
		}
	}

}