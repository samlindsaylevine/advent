package advent.year2015.day23;

import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableMap;

public class AssemblyComputer {

	private final Map<String, Register> registers;

	public AssemblyComputer() {
		this.registers = ImmutableMap.of( //
				"a", new Register(), //
				"b", new Register());
	}

	public static class Register {
		private int value = 0;

		public int getValue() {
			return this.value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return String.valueOf(this.value);
		}
	}

	public Register getRegister(String label) {
		Register output = this.registers.get(label);
		return Objects.requireNonNull(output);
	}

	@Override
	public String toString() {
		return this.registers.toString();
	}

}
