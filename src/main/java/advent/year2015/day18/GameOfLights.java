package advent.year2015.day18;

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

/**
 * --- Day 18: Like a GIF For Your Yard ---
 * After the million lights incident, the fire code has gotten stricter: now, at most ten thousand lights are allowed. 
 * You arrange them in a 100x100 grid.
 * Never one to let you down, Santa again mails you instructions on the ideal lighting configuration.  With so few
 * lights, he says, you'll have to resort to animation.
 * Start by setting your lights to the included initial configuration (your puzzle input).  A # means "on", and a .
 * means "off".
 * Then, animate your grid in steps, where each step decides the next configuration based on the current one.  Each
 * light's next state (either on or off) depends on its current state and the current states of the eight lights
 * adjacent to it (including diagonals).  Lights on the edge of the grid might have fewer than eight neighbors; the
 * missing ones always count as "off".
 * For example, in a simplified 6x6 grid, the light marked A has the neighbors numbered 1 through 8, and the light
 * marked B, which is on an edge, only has the neighbors marked 1 through 5:
 * 1B5...
 * 234...
 * ......
 * ..123.
 * ..8A4.
 * ..765.
 * 
 * The state a light should have next is based on its current state (on or off) plus the number of neighbors that are
 * on:
 * 
 * A light which is on stays on when 2 or 3 neighbors are on, and turns off otherwise.
 * A light which is off turns on if exactly 3 neighbors are on, and stays off otherwise.
 * 
 * All of the lights update simultaneously; they all consider the same current state before moving to the next.
 * Here's a few steps from an example configuration of another 6x6 grid:
 * Initial state:
 * .#.#.#
 * ...##.
 * #....#
 * ..#...
 * #.#..#
 * ####..
 * 
 * After 1 step:
 * ..##..
 * ..##.#
 * ...##.
 * ......
 * #.....
 * #.##..
 * 
 * After 2 steps:
 * ..###.
 * ......
 * ..###.
 * ......
 * .#....
 * .#....
 * 
 * After 3 steps:
 * ...#..
 * ......
 * ...#..
 * ..##..
 * ......
 * ......
 * 
 * After 4 steps:
 * ......
 * ......
 * ..##..
 * ..##..
 * ......
 * ......
 * 
 * After 4 steps, this example has four lights on.
 * In your grid of 100x100 lights, given your initial configuration, how many lights are on after 100 steps?
 * 
 * --- Part Two ---
 * You flip the instructions over; Santa goes on to point out that this is all just an implementation of Conway's Game
 * of Life.  At least, it was, until you notice that something's wrong with the grid of lights you bought: four lights,
 * one in each corner, are stuck on and can't be turned off.  The example above will actually run like this:
 * Initial state:
 * ##.#.#
 * ...##.
 * #....#
 * ..#...
 * #.#..#
 * ####.#
 * 
 * After 1 step:
 * #.##.#
 * ####.#
 * ...##.
 * ......
 * #...#.
 * #.####
 * 
 * After 2 steps:
 * #..#.#
 * #....#
 * .#.##.
 * ...##.
 * .#..##
 * ##.###
 * 
 * After 3 steps:
 * #...##
 * ####.#
 * ..##.#
 * ......
 * ##....
 * ####.#
 * 
 * After 4 steps:
 * #.####
 * #....#
 * ...#..
 * .##...
 * #.....
 * #.#..#
 * 
 * After 5 steps:
 * ##.###
 * .##..#
 * .##...
 * .##...
 * #.#...
 * ##...#
 * 
 * After 5 steps, this example now has 17 lights on.
 * In your grid of 100x100 lights, given your initial configuration, but with the four corners always in the on state,
 * how many lights are on after 100 steps?
 * 
 */
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
		String fileContents = Files.toString(new File("src/main/java/advent/year2015/day18/input.txt"), StandardCharsets.UTF_8);
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