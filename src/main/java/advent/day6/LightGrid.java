package advent.day6;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LightGrid {

	private static final int SIZE = 1_000;

	private Light[][] lights = new Light[SIZE][SIZE];

	public LightGrid(Supplier<Light> lightConstructor) {
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				this.lights[i][j] = lightConstructor.get();
			}
		}
	}

	public void execute(Instruction instruction) {
		for (int i = instruction.minX; i <= instruction.maxX; i++) {
			for (int j = instruction.minY; j <= instruction.maxY; j++) {
				instruction.action.accept(this.lights[i][j]);
			}
		}
	}

	public long count() {
		return Arrays.stream(this.lights) //
				.flatMap(Arrays::stream) //
				.mapToLong(Light::onCount) //
				.sum();
	}

	static long countWithDigitalLights() throws IOException {
		return countAfterInstructions(DigitalLight::new);
	}

	static long countWithAnalogLights() throws IOException {
		return countAfterInstructions(AnalogLight::new);
	}

	private static long countAfterInstructions(Supplier<Light> lightConstructor) throws IOException {
		LightGrid grid = new LightGrid(lightConstructor);

		Files.lines(Paths.get("src/advent/day6/input.txt")) //
				.map(Instruction::new) //
				.forEach(grid::execute);

		return grid.count();
	}

	public static void main(String[] args) throws IOException {
		System.out.println(countWithAnalogLights());
	}

	static class Instruction {
		final int minX;
		final int minY;
		final int maxX;
		final int maxY;
		final Consumer<Light> action;

		public Instruction(String representation) {
			if (representation.startsWith("turn on")) {
				this.action = Light::on;
			} else if (representation.startsWith("turn off")) {
				this.action = Light::off;
			} else if (representation.startsWith("toggle")) {
				this.action = Light::toggle;
			} else {
				throw new IllegalArgumentException(representation);
			}

			Pattern numbersPattern = Pattern.compile("(\\d+),(\\d+) through (\\d+),(\\d+)");
			Matcher matcher = numbersPattern.matcher(representation);
			matcher.find();

			this.minX = Integer.valueOf(matcher.group(1));
			this.minY = Integer.valueOf(matcher.group(2));
			this.maxX = Integer.valueOf(matcher.group(3));
			this.maxY = Integer.valueOf(matcher.group(4));
		}
	}

	public static interface Light {
		void on();

		void off();

		void toggle();

		long onCount();
	}

	public static class DigitalLight implements Light {

		private boolean isOn = false;

		@Override
		public void on() {
			this.isOn = true;
		}

		@Override
		public void off() {
			this.isOn = false;
		}

		@Override
		public void toggle() {
			this.isOn = !this.isOn;
		}

		@Override
		public long onCount() {
			return this.isOn ? 1 : 0;
		}
	}

	public static class AnalogLight implements Light {

		private int brightness = 0;

		@Override
		public void on() {
			this.brightness++;
		}

		@Override
		public void off() {
			this.brightness--;
			if (this.brightness < 0) {
				this.brightness = 0;
			}
		}

		@Override
		public void toggle() {
			this.brightness += 2;
		}

		@Override
		public long onCount() {
			return this.brightness;
		}
	}

}
