package advent.year2016.day12;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static advent.utils.CollectorUtils.toArrayList;

/**
 * --- Day 12: Leonardo's Monorail ---
 * You finally reach the top floor of this building: a garden with a slanted glass ceiling. Looks like there are no
 * more stars to be had.
 * While sitting on a nearby bench amidst some tiger lilies, you manage to decrypt some of the files you extracted from
 * the servers downstairs.
 * According to these documents, Easter Bunny HQ isn't just this building - it's a collection of buildings in the
 * nearby area. They're all connected by a local monorail, and there's another building not far from here!
 * Unfortunately, being night, the monorail is currently not operating.
 * You remotely connect to the monorail control systems and discover that the boot sequence expects a password. The
 * password-checking logic (your puzzle input) is easy to extract, but the code it uses is strange: it's assembunny
 * code designed for the new computer you just assembled. You'll have to execute the code and get the password.
 * The assembunny code you've extracted operates on four registers (a, b, c, and d) that start at 0 and can hold any
 * integer. However, it seems to make use of only a few instructions:
 *
 * cpy x y copies x (either an integer or the value of a register) into register y.
 * inc x increases the value of register x by one.
 * dec x decreases the value of register x by one.
 * jnz x y jumps to an instruction y away (positive means forward; negative means backward), but only if x is not zero.
 *
 * The jnz instruction moves relative to itself: an offset of -1 would continue at the previous instruction, while an
 * offset of 2 would skip over the next instruction.
 * For example:
 * cpy 41 a
 * inc a
 * inc a
 * dec a
 * jnz a 2
 * dec a
 *
 * The above code would set register a to 41, increase its value by 2, decrease its value by 1, and then skip the last
 * dec a (because a is not zero, so the jnz a 2 skips it), leaving register a at 42. When you move past the last
 * instruction, the program halts.
 * After executing the assembunny code in your puzzle input, what value is left in register a?
 *
 * --- Part Two ---
 * As you head down the fire escape to the monorail, you notice it didn't start; register c needs to be initialized to
 * the position of the ignition key.
 * If you instead initialize register c to be 1, what value is now left in register a?
 *
 * ---
 *
 * This class has been extended on day 23 to also handle the "toggle"
 * instruction, and on day 25 for "out".
 */
public class AssembunnyComputer {

    private Map<String, Long> registers = new HashMap<>();

    private List<AssembunnyInstruction> instructions;
    int instructionPointer = 0;

    private List<Long> output = new ArrayList<>();

    /**
     * Several instructions want to be able to reference either a register
     * (alphabetic) or a fixed value (numeric).
     */
    public long getValue(String name) {
        try {
            return Long.parseLong(name);
        } catch (NumberFormatException e) {
            return this.registers.computeIfAbsent(name, any -> 0L);
        }
    }

    public List<Long> getOutput() {
        return output;
    }

    public void setRegister(String name, long value) {
        this.registers.put(name, value);
    }

    public void executeProgram(List<String> program) {
        this.executeProgram(program, computer -> false);
    }

    /**
     * @param program
     *            The string representation of the lines to run.
     * @param haltCondition
     *            An extra halt condition - if this becomes true, we will not
     *            continue executing the program. (This is used particularly for
     *            Day 25, stopping after we have amassed a certain amount of
     *            output.)
     */
    public void executeProgram(List<String> program, Predicate<AssembunnyComputer> haltCondition) {

        int stepsTaken = 0;

        this.instructions = program.stream() //
                .map(AssembunnyInstruction::of) //
                .collect(toArrayList());

        this.instructionPointer = 0;

        while (instructionPointer < instructions.size()) {

            if (haltCondition.test(this)) {
                return;
            }

            // Debug output if interested.
            stepsTaken++;
            if (stepsTaken % 1000000 == 0) {
                System.out.println("State " + this.registers);
                System.out.println("Pointer " + instructionPointer);
                System.out.println("Executing " + program.get(instructionPointer));
                System.out.println();
            }

            instructionPointer += instructions.get(instructionPointer).execute(this);
        }
    }

    private static class AssembunnyInstruction {

        private final Consumer<AssembunnyComputer> mutateComputer;
        private final Function<AssembunnyComputer, Long> stepsToAdvance;
        private final Supplier<AssembunnyInstruction> toggledVersion;

        public static AssembunnyInstruction of(String representation) {
            return Arrays.stream(OpCodes.values()) //
                    .filter(code -> code.matches(representation)) //
                    .map(code -> code.create(representation)) //
                    .findFirst() //
                    .orElseThrow(() -> new IllegalArgumentException("Bad instruction " + representation));
        }

        private static AssembunnyInstruction acting(Consumer<AssembunnyComputer> mutateComputer,
                                                    Supplier<AssembunnyInstruction> toggledVersion) {
            return new AssembunnyInstruction(mutateComputer, any -> 1L, toggledVersion);
        }

        private static AssembunnyInstruction stepping(Function<AssembunnyComputer, Long> stepsToAdvance,
                                                      Supplier<AssembunnyInstruction> toggledVersion) {
            return new AssembunnyInstruction(computer -> {
            }, stepsToAdvance, toggledVersion);
        }

        private AssembunnyInstruction(Consumer<AssembunnyComputer> mutateComputer,
                                      Function<AssembunnyComputer, Long> stepsToAdvance, Supplier<AssembunnyInstruction> toggledVersion) {
            super();
            this.mutateComputer = mutateComputer;
            this.stepsToAdvance = stepsToAdvance;
            this.toggledVersion = toggledVersion;
        }

        public long execute(AssembunnyComputer computer) {
            this.mutateComputer.accept(computer);
            return this.stepsToAdvance.apply(computer);
        }
    }

    private static enum OpCodes {

        CPY("cpy (-?\\w+) (\\w+)") {
            @Override
            protected AssembunnyInstruction create(Matcher matchResult) {
                String from = matchResult.group(1);
                String to = matchResult.group(2);
                return AssembunnyInstruction.acting(computer -> computer.setRegister(to, computer.getValue(from)),
                        () -> JMP.create(matchResult));
            }
        }, //

        INC("inc (\\w+)") {
            @Override
            protected AssembunnyInstruction create(Matcher matchResult) {
                String register = matchResult.group(1);
                return AssembunnyInstruction.acting(
                        computer -> computer.setRegister(register, computer.getValue(register) + 1),
                        () -> DEC.create(matchResult));
            }
        }, //

        DEC("dec (\\w+)") {
            @Override
            protected AssembunnyInstruction create(Matcher matchResult) {

                String register = matchResult.group(1);
                return AssembunnyInstruction.acting(
                        computer -> computer.setRegister(register, computer.getValue(register) - 1),
                        () -> INC.create(matchResult));
            }
        }, //

        JMP("jnz (\\w+) (-?\\w+)") {
            @Override
            protected AssembunnyInstruction create(Matcher matchResult) {
                String value = matchResult.group(1);
                String jumpStr = matchResult.group(2);

                return AssembunnyInstruction.stepping(
                        computer -> computer.getValue(value) == 0 ? 1 : computer.getValue(jumpStr),
                        () -> OpCodes.CPY.create(matchResult));
            }
        },

        TGL("tgl (\\w+)") {
            @Override
            protected AssembunnyInstruction create(Matcher matchResult) {
                String stepsStr = matchResult.group(1);
                return AssembunnyInstruction.acting(computer -> {
                    long steps = computer.getValue(stepsStr);
                    int index = (int) (computer.instructionPointer + steps);
                    if (index < 0 || index >= computer.instructions.size()) {
                        return;
                    }

                    AssembunnyInstruction existing = computer.instructions.get(index);
                    computer.instructions.set(index, existing.toggledVersion.get());
                }, () -> INC.create(matchResult));
            }

        },

        OUT("out (\\w+)") {
            @Override
            protected AssembunnyInstruction create(Matcher matchResult) {
                String value = matchResult.group(1);
                return AssembunnyInstruction.acting(computer -> computer.output.add(computer.getValue(value)),
                        () -> INC.create(matchResult));
            }

        };

        private OpCodes(String regex) {
            this.pattern = Pattern.compile(regex);
        }

        private final Pattern pattern;

        public boolean matches(String representation) {
            return this.pattern.matcher(representation).matches();
        }

        public AssembunnyInstruction create(String input) {
            Matcher matcher = this.pattern.matcher(input);
            Preconditions.checkArgument(matcher.matches());
            return this.create(matcher);
        }

        protected abstract AssembunnyInstruction create(Matcher matchResult);
    }

    public static void main(String[] args) throws IOException {
        Path inputFilePath = Paths.get("src/main/java/advent/year2016/day12/input.txt");

        AssembunnyComputer computer = new AssembunnyComputer();

        List<String> lines = Files.readAllLines(inputFilePath);
        computer.executeProgram(lines);
        System.out.println(computer.getValue("a"));

        AssembunnyComputer partTwo = new AssembunnyComputer();
        partTwo.setRegister("c", 1);
        partTwo.executeProgram(lines);
        System.out.println(partTwo.getValue("a"));
    }
}