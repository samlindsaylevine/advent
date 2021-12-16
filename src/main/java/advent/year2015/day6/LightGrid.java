package advent.year2015.day6;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * --- Day 6: Probably a Fire Hazard ---
 * Because your neighbors keep defeating you in the holiday house decorating contest year after year, you've decided to
 * deploy one million lights in a 1000x1000 grid.
 * Furthermore, because you've been especially nice this year, Santa has mailed you instructions on how to display the
 * ideal lighting configuration.
 * Lights in your grid are numbered from 0 to 999 in each direction; the lights at each corner are at 0,0, 0,999,
 * 999,999, and 999,0. The instructions include whether to turn on, turn off, or toggle various inclusive ranges given
 * as coordinate pairs.  Each coordinate pair represents opposite corners of a rectangle, inclusive; a coordinate pair
 * like 0,0 through 2,2 therefore refers to 9 lights in a 3x3 square.  The lights all start turned off.
 * To defeat your neighbors this year, all you have to do is set up your lights by doing the instructions Santa sent
 * you in order.
 * For example:
 * 
 * turn on 0,0 through 999,999 would turn on (or leave on) every light.
 * toggle 0,0 through 999,0 would toggle the first line of 1000 lights, turning off the ones that were on, and turning
 * on the ones that were off.
 * turn off 499,499 through 500,500 would turn off (or leave off) the middle four lights.
 * 
 * After following the instructions, how many lights are lit?
 * 
 * --- Part Two ---
 * You just finish implementing your winning light pattern when you realize you mistranslated Santa's message from
 * Ancient Nordic Elvish.
 * The light grid you bought actually has individual brightness controls; each light can have a brightness of zero or
 * more.  The lights all start at zero.
 * The phrase turn on actually means that you should increase the brightness of those lights by 1.
 * The phrase turn off actually means that you should decrease the brightness of those lights by 1, to a minimum of
 * zero.
 * The phrase toggle actually means that you should increase the brightness of those lights by 2.
 * What is the total brightness of all lights combined after following Santa's instructions?
 * For example:
 * 
 * turn on 0,0 through 0,0 would increase the total brightness by 1.
 * toggle 0,0 through 999,999 would increase the total brightness by 2000000.
 * 
 * 
 */
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

		Files.lines(Paths.get("src/main/java/advent/year2015/day6/input.txt")) //
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