package advent.day18;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.io.Files;

public class GameOfLights {

	private List<List<Light>> lights;

	public GameOfLights(String representation) {
		this.lights = Arrays.stream(representation.trim().split("\n")) //
				.map(GameOfLights::toRow) //
				.collect(toList());
	}

	private GameOfLights(List<List<Light>> lights) {
		this.lights = lights;
	}

	public void makeCornersStuck() {
		List<Light> firstRow = this.lights.get(0);
		List<Light> lastRow = this.lights.get(this.lights.size() - 1);
		this.makeStuck(firstRow);
		this.makeStuck(lastRow);
	}

	private void makeStuck(List<Light> lights) {
		lights.get(0).isStuck = true;
		lights.get(0).isOn = true;
		lights.get(lights.size() - 1).isStuck = true;
		lights.get(lights.size() - 1).isOn = true;
	}

	private static List<Light> toRow(String representation) {
		return representation.chars() //
				.mapToObj(c -> c == '#' ? true : false) //
				.map(Light::new) //
				.collect(toList());
	}

	public GameOfLights step() {
		return this.step(GameOfLights::defaultRule);
	}

	public GameOfLights step(Rule neighborBasedRule) {
		List<List<Light>> newLights = new ArrayList<>();

		for (int y = 0; y < this.lights.size(); y++) {
			List<Light> row = this.lights.get(y);
			List<Light> newRow = new ArrayList<>();
			for (int x = 0; x < row.size(); x++) {
				Light light = row.get(x);
				newRow.add(new Light(neighborBasedRule.survives(light, this.neighbors(x, y)), light.isStuck));
			}
			newLights.add(newRow);
		}

		return new GameOfLights(newLights);
	}

	@FunctionalInterface
	private interface Rule {
		public boolean survives(Light light, Set<Light> neighbors);
	}

	private static boolean defaultRule(Light light, Set<Light> neighbors) {
		long neighborCount = neighbors.stream().filter(Light::isOn).count();

		if (light.isOn() && (neighborCount == 2 || neighborCount == 3)) {
			return true;
		} else if (!light.isOn() && neighborCount == 3) {
			return true;
		} else if (light.isStuck) {
			return true;
		} else {
			return false;
		}
	}

	private Set<Light> neighbors(int x, int y) {
		return Stream
				.of(this.get(x - 1, y - 1), //
						this.get(x, y - 1), //
						this.get(x + 1, y - 1), //
						this.get(x - 1, y), //
						this.get(x + 1, y), //
						this.get(x - 1, y + 1), //
						this.get(x, y + 1), //
						this.get(x + 1, y + 1)) //
				.filter(Optional::isPresent) //
				.map(Optional::get) //
				.collect(toSet());
	}

	/**
	 * Gets the light if in bounds, or empty if out of bounds.
	 */
	private Optional<Light> get(int x, int y) {
		if (x < 0 || y < 0 || y >= this.lights.size()) {
			return Optional.empty();
		}

		List<Light> row = this.lights.get(y);

		if (x >= this.lights.size()) {
			return Optional.empty();
		}

		return Optional.of(row.get(x));
	}

	public long count() {
		return this.lights.stream() //
				.flatMap(List::stream) //
				.filter(Light::isOn) //
				.count();
	}

	@Override
	public String toString() {
		return this.lights.stream() //
				.map(GameOfLights::rowToString) //
				.collect(joining("\n"));
	}

	private static String rowToString(List<Light> lights) {
		return lights.stream() //
				.map(l -> l.isOn ? "#" : ".") //
				.collect(joining(""));
	}

	private static class Light {
		private boolean isOn;
		private boolean isStuck;

		public Light(boolean isOn) {
			this(isOn, false);
		}

		public Light(boolean isOn, boolean isStuck) {
			this.isOn = isOn;
			this.isStuck = isStuck;
		}

		public boolean isOn() {
			return this.isOn;
		}

	}

	private static GameOfLights fromFile() throws IOException {
		String fileContents = Files.toString(new File("src/main/java/advent/day18/input.txt"), StandardCharsets.UTF_8);
		return new GameOfLights(fileContents);
	}

	private static long onAfterSteps(GameOfLights game, int steps) {
		for (int i = 0; i < steps; i++) {
			game = game.step();
		}
		return game.count();
	}

	public static void main(String[] args) throws IOException {
		GameOfLights game = fromFile();
		game.makeCornersStuck();
		System.out.println(onAfterSteps(game, 100));
	}

}
