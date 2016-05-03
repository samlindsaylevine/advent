package advent.day23;

import java.util.Optional;
import java.util.Set;

import org.reflections.Reflections;

public abstract class Instruction {

	public int nextInstructionIndexDelta(AssemblyComputer computer) {
		return 1;
	}

	public void updateComputer(AssemblyComputer computer) {

	}

	protected static Optional<? extends Instruction> tryParse(String s) {
		return Optional.empty();
	}

	public static Instruction fromString(String string) {
		Reflections reflections = new Reflections("advent.day23.instructions");

		Set<Class<? extends Instruction>> instructionTypes = reflections.getSubTypesOf(Instruction.class);

		return instructionTypes.stream() //
				.map(i -> tryParse(i, string)) //
				.filter(Optional::isPresent) //
				.map(Optional::get) //
				.findFirst() //
				.orElseThrow(() -> new IllegalArgumentException(string));
	}

	@SuppressWarnings("unchecked")
	private static Optional<Instruction> tryParse(Class<?> clazz, String string) {
		try {
			return (Optional<Instruction>) clazz.getMethod("tryParse", String.class) //
					.invoke(null, string);
		} catch (ReflectiveOperationException | ClassCastException e) {
			return Optional.empty();
		}
	}
}
