package advent.year2015.day12;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.google.common.io.Files;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

/**
 * I'm not super proud of this one.
 */
public class JsonDocument {

	private final int sumOfAllNumbers;
	private final int sumOfAllNonRedNumbers;

	public JsonDocument(String input) {
		try (JsonReader reader = new JsonReader(new StringReader(input))) {

			SumResult sums = sumAllNumbers(reader);
			this.sumOfAllNumbers = sums.all;
			this.sumOfAllNonRedNumbers = sums.nonRed;

		} catch (IOException e) {
			// Should never happen.
			throw new RuntimeException(e);
		}

	}

	private static enum NestedJsonType {
		ARRAY, OBJECT;
	}

	private static SumResult sumAllNumbers(JsonReader reader) throws IOException {
		int all = 0;
		int nonRed = 0;

		Stack<NestedJsonType> nestedTypes = new Stack<>();
		// Stacks maintained one at each level of object.
		List<Boolean> areObjectsRed = new ArrayList<>();
		List<Integer> nestedNumbers = new ArrayList<>();

		while (true) {
			JsonToken token = reader.peek();
			switch (token) {
			case BEGIN_ARRAY:
				reader.beginArray();
				nestedTypes.push(NestedJsonType.ARRAY);
				break;
			case END_ARRAY:
				reader.endArray();
				nestedTypes.pop();
				break;
			case BEGIN_OBJECT:
				reader.beginObject();
				nestedTypes.push(NestedJsonType.OBJECT);
				areObjectsRed.add(false);
				nestedNumbers.add(0);
				break;
			case END_OBJECT:
				reader.endObject();
				nestedTypes.pop();
				boolean wasRed = areObjectsRed.remove(areObjectsRed.size() - 1);
				int nestedNumber = nestedNumbers.remove(nestedNumbers.size() - 1);
				if (!wasRed && nestedNumbers.isEmpty()) {
					nonRed += nestedNumber;
				} else if (!wasRed) {
					nestedNumbers.set(nestedNumbers.size() - 1,
							nestedNumbers.get(nestedNumbers.size() - 1) + nestedNumber);
				}
				break;
			case NAME:
				reader.nextName();
				break;
			case STRING:
				String value = reader.nextString();
				if (nestedTypes.peek() == NestedJsonType.OBJECT && value.equals("red") && !areObjectsRed.isEmpty()) {
					areObjectsRed.set(areObjectsRed.size() - 1, true);
				}
				break;
			case NUMBER:
				int nextInt = reader.nextInt();
				all += nextInt;
				if (nestedNumbers.isEmpty()) {
					nonRed += nextInt;
				} else {
					nestedNumbers.set(nestedNumbers.size() - 1, nestedNumbers.get(nestedNumbers.size() - 1) + nextInt);
				}
				break;
			case BOOLEAN:
				reader.nextBoolean();
				break;
			case NULL:
				reader.nextNull();
				break;
			case END_DOCUMENT:
				return new SumResult(all, nonRed);
			}
		}
	}

	public int sumAllNumbers() {
		return this.sumOfAllNumbers;
	}

	public int sumAllNonRedNumbers() {
		return this.sumOfAllNonRedNumbers;
	}

	private static class SumResult {
		private final int all;
		private final int nonRed;

		public SumResult(int all, int nonRed) {
			this.all = all;
			this.nonRed = nonRed;
		}
	}

	public static void main(String[] args) throws IOException {
		String input = Files.toString(new File("src/main/java/advent/day12/input.txt"), StandardCharsets.UTF_8);
		JsonDocument document = new JsonDocument(input);
		System.out.println(document.sumOfAllNumbers);
		System.out.println(document.sumOfAllNonRedNumbers);
	}

}
