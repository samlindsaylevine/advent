package advent.year2016.day10;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

/**
 * --- Day 10: Balance Bots ---
 * You come upon a factory in which many robots are zooming around handing small microchips to each other.
 * Upon closer examination, you notice that each bot only proceeds when it has two microchips, and once it does, it
 * gives each one to a different bot or puts it in a marked "output" bin. Sometimes, bots take microchips from "input"
 * bins, too.
 * Inspecting one of the microchips, it seems like they each contain a single number; the bots must use some logic to
 * decide what to do with each chip. You access the local control computer and download the bots' instructions (your
 * puzzle input).
 * Some of the instructions specify that a specific-valued microchip should be given to a specific bot; the rest of the
 * instructions indicate what a given bot should do with its lower-value or higher-value chip.
 * For example, consider the following instructions:
 * value 5 goes to bot 2
 * bot 2 gives low to bot 1 and high to bot 0
 * value 3 goes to bot 1
 * bot 1 gives low to output 1 and high to bot 0
 * bot 0 gives low to output 2 and high to output 0
 * value 2 goes to bot 2
 * 
 * 
 * Initially, bot 1 starts with a value-3 chip, and bot 2 starts with a value-2 chip and a value-5 chip.
 * Because bot 2 has two microchips, it gives its lower one (2) to bot 1 and its higher one (5) to bot 0.
 * Then, bot 1 has two microchips; it puts the value-2 chip in output 1 and gives the value-3 chip to bot 0.
 * Finally, bot 0 has two microchips; it puts the 3 in output 2 and the 5 in output 0.
 * 
 * In the end, output bin 0 contains a value-5 microchip, output bin 1 contains a value-2 microchip, and output bin 2
 * contains a value-3 microchip. In this configuration, bot number 2 is responsible for comparing value-5 microchips
 * with value-2 microchips.
 * Based on your instructions, what is the number of the bot that is responsible for comparing value-61 microchips with
 * value-17 microchips?
 * 
 * --- Part Two ---
 * What do you get if you multiply together the values of one chip in each of outputs 0, 1, and 2?
 * 
 */
public class MicrochipFactory {

	private Map<Integer, OutputBin> binsByNumber = new HashMap<>();
	private Map<Integer, Bot> botsByNumber = new HashMap<>();

	private static final Pattern ASSIGN_CHIP_PATTERN = Pattern.compile("value (\\d+) goes to bot (\\d+)");
	private static final Pattern BOT_INSTRUCTION_PATTERN = Pattern
			.compile("bot (\\d+) gives low to (\\w+) (\\d+) and high to (\\w+) (\\d+)");

	public void addInstruction(String instruction) {
		Matcher assignMatcher = ASSIGN_CHIP_PATTERN.matcher(instruction);
		if (assignMatcher.matches()) {
			int value = Integer.valueOf(assignMatcher.group(1));
			int botNumber = Integer.valueOf(assignMatcher.group(2));
			this.getBot(botNumber).addChip(value);
			return;
		}

		Matcher botInstructionMatcher = BOT_INSTRUCTION_PATTERN.matcher(instruction);
		if (botInstructionMatcher.matches()) {
			int botNumber = Integer.valueOf(botInstructionMatcher.group(1));

			String lowTargetType = botInstructionMatcher.group(2);
			int lowTargetNumber = Integer.valueOf(botInstructionMatcher.group(3));
			ChipHolder lowTarget = this.getTarget(lowTargetType, lowTargetNumber);

			String highTargetType = botInstructionMatcher.group(4);
			int highTargetNumber = Integer.valueOf(botInstructionMatcher.group(5));
			ChipHolder highTarget = this.getTarget(highTargetType, highTargetNumber);

			this.getBot(botNumber).addTargets(lowTarget, highTarget);

			return;
		}

		throw new IllegalArgumentException("Unrecognized instruction " + instruction);
	}

	private ChipHolder getTarget(String targetType, int number) {
		if (targetType.equals("bot")) {
			return this.getBot(number);
		} else if (targetType.equals("output")) {
			return this.getBin(number);
		} else {
			throw new IllegalArgumentException("Wrong target type " + targetType);
		}
	}

	public OutputBin getBin(int number) {
		return this.binsByNumber.computeIfAbsent(number, OutputBin::new);
	}

	private Bot getBot(int number) {
		return this.botsByNumber.computeIfAbsent(number, Bot::new);
	}

	public int findNumberOfBotWhoHandled(int chipOne, int chipTwo) {
		Set<Integer> desired = ImmutableSet.of(chipOne, chipTwo);

		return this.botsByNumber.values().stream() //
				.filter(bot -> bot.chips.equals(desired)) //
				.findFirst() //
				.orElseThrow(() -> new NoSuchElementException("No bot handled " + desired)) //
				.number;
	}

	private static interface ChipHolder {
		public void addChip(int chipNumber);
	}

	public static class OutputBin implements ChipHolder {

		private final int number;
		private Set<Integer> chips = new HashSet<>();

		public OutputBin(int number) {
			this.number = number;
		}

		@Override
		public void addChip(int chipNumber) {
			this.chips.add(chipNumber);
		}

		public Set<Integer> getChips() {
			return ImmutableSet.copyOf(this.chips);
		}

		public int getNumber() {
			return this.number;
		}
	}

	private static class Bot implements ChipHolder {

		private final int number;
		private TreeSet<Integer> chips = new TreeSet<>();
		private Optional<Targets> targets = Optional.empty();

		public Bot(int number) {
			this.number = number;
		}

		@Override
		public void addChip(int chipNumber) {
			this.chips.add(chipNumber);
			this.attemptAction();
		}

		public void addTargets(ChipHolder lowTarget, ChipHolder highTarget) {
			this.targets = Optional.of(new Targets(lowTarget, highTarget));
			this.attemptAction();
		}

		private void attemptAction() {
			Preconditions.checkState(this.chips.size() <= 2);

			if (this.chips.size() == 2) {
				this.targets.ifPresent(t -> {
					Iterator<Integer> chipIterator = this.chips.iterator();
					t.lowTarget.addChip(chipIterator.next());
					t.highTarget.addChip(chipIterator.next());
				});
			}
		}

		private static class Targets {

			public final ChipHolder lowTarget;
			public final ChipHolder highTarget;

			public Targets(ChipHolder lowTarget, ChipHolder highTarget) {
				this.lowTarget = lowTarget;
				this.highTarget = highTarget;
			}
		}

	}

	public static void main(String[] args) throws IOException {
		Path inputFilePath = Paths.get("src/main/java/advent/year2016/day10/input.txt");

		MicrochipFactory factory = new MicrochipFactory();

		try (Stream<String> lines = Files.lines(inputFilePath)) {
			lines.forEach(factory::addInstruction);
		}

		System.out.println(factory.findNumberOfBotWhoHandled(61, 17));

		// We're just assuming that there is only one chip in each of bins 0, 1,
		// and 2, because otherwise the answer is indeterminate. Let's validate
		// that just to be sure.
		IntStream.range(0, 3) //
				.mapToObj(factory::getBin) //
				.map(OutputBin::getChips) //
				.forEach(chips -> Preconditions.checkState(chips.size() == 1));

		// Now multiply 'em all together.
		int product = IntStream.range(0, 3) //
				.mapToObj(factory::getBin) //
				.map(OutputBin::getChips) //
				.flatMap(Set::stream) //
				.reduce(1, (x, y) -> x * y);

		System.out.println(product);
	}

}