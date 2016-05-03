package advent.day1;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import com.google.common.io.Files;

public class DeliveryInstructions {

	private final String rawString;

	public DeliveryInstructions(String representation) {
		this.rawString = representation;
	}

	public long finalFloor() {
		Integer leftParen = (int) '(';
		Integer rightParen = (int) ')';
		return this.rawString.chars().filter(leftParen::equals).count() //
				- this.rawString.chars().filter(rightParen::equals).count();
	}

	public Optional<Integer> enterTheBasementStep() {
		int position = 0;

		for (int i = 0; i < this.rawString.length(); i++) {
			if (this.rawString.charAt(i) == '(') {
				position++;
			} else if (this.rawString.charAt(i) == ')') {
				position--;
			}

			if (position < 0) {
				// 1-indexed output is required.
				return Optional.of(i + 1);
			}
		}

		return Optional.empty();
	}

	public static DeliveryInstructions fromFile() throws IOException {
		return new DeliveryInstructions(
				Files.toString(new File("src/main/java/advent/day1/input.txt"), StandardCharsets.UTF_8));
	}

	public static void main(String[] args) throws IOException {
		System.out.println(fromFile().enterTheBasementStep());
	}
}
