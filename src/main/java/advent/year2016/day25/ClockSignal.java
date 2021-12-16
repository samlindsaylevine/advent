package advent.year2016.day25;

import advent.year2016.day12.AssembunnyComputer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Mostly just extended the existing {@link AssembunnyComputer}.
 *
 * --- Day 25: Clock Signal ---
 * You open the door and find yourself on the roof. The city sprawls away from you for miles and miles.
 * There's not much time now - it's already Christmas, but you're nowhere near the North Pole, much too far to deliver
 * these stars to the sleigh in time.
 * However, maybe the huge antenna up here can offer a solution. After all, the sleigh doesn't need the stars, exactly;
 * it needs the timing data they provide, and you happen to have a massive signal generator right here.
 * You connect the stars you have to your prototype computer, connect that to the antenna, and begin the transmission.
 * Nothing happens.
 * You call the service number printed on the side of the antenna and quickly explain the situation. "I'm not sure what
 * kind of equipment you have connected over there," he says, "but you need a clock signal." You try to explain that
 * this is a signal for a clock.
 * "No, no, a clock signal - timing information so the antenna computer knows how to read the data you're sending it.
 * An endless, alternating pattern of 0, 1, 0, 1, 0, 1, 0, 1, 0, 1...." He trails off.
 * You ask if the antenna can handle a clock signal at the frequency you would need to use for the data from the stars.
 * "There's no way it can! The only antenna we've installed capable of that is on top of a top-secret Easter Bunny
 * installation, and you're definitely not-" You hang up the phone.
 * You've extracted the antenna's clock signal generation assembunny code (your puzzle input); it looks mostly
 * compatible with code you worked on just recently.
 * This antenna code, being a signal generator, uses one extra instruction:
 *
 * out x transmits x (either an integer or the value of a register) as the next value for the clock signal.
 *
 * The code takes a value (via register a) that describes the signal to generate, but you're not sure how it's used.
 * You'll have to find the input to produce the right signal through experimentation.
 * What is the lowest positive integer that can be used to initialize register a and cause the code to output a clock
 * signal of 0, 1, 0, 1... repeating forever?
 *
 * --- Part Two ---
 * The antenna is ready. Now, all you need is the fifty stars required to generate the signal for the sleigh, but you
 * don't have enough.
 * You look toward the sky in desperation... suddenly noticing that a lone star has been installed at the top of the
 * antenna!  Only 49 more to go.
 *
 */
public class ClockSignal {

    private static final int SAMPLE_OUTPUT_SIZE = 10;

    private final List<String> instructions;

    public ClockSignal(List<String> instructions) {
        this.instructions = instructions;
    }

    private static boolean isDesiredOutput(List<Long> list) {
        // Debug information if desired.
        // System.out.println(list.stream().map(Object::toString).collect(joining("")));
        return IntStream.range(0, list.size()) //
                .allMatch(i -> list.get(i) == i % 2);
    }

    private List<Long> output(int initialValue) {
        AssembunnyComputer computer = new AssembunnyComputer();
        computer.setRegister("a", initialValue);
        computer.executeProgram(instructions, c -> c.getOutput().size() >= SAMPLE_OUTPUT_SIZE);
        return computer.getOutput();
    }

    public int desiredinitialValue() {
        return IntStream.iterate(0, i -> i + 1) //
                // Debug information if desired.
                // .peek(System.out::println) //
                .filter(i -> isDesiredOutput(this.output(i))) //
                .findFirst() //
                .getAsInt();
    }

    public static void main(String[] args) throws IOException {
        Path inputFilePath = Paths.get("src/main/java/advent/year2016/day25/input.txt");

        List<String> lines = Files.readAllLines(inputFilePath);
        ClockSignal signal = new ClockSignal(lines);
        System.out.println(signal.desiredinitialValue());
    }

}