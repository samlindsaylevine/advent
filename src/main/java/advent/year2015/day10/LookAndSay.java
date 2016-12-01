package advent.year2015.day10;

public class LookAndSay {

	public static String lookAndSay(String input) {
		if (input.isEmpty()) {
			return "";
		}

		StringBuilder output = new StringBuilder();

		String currentDigit = input.substring(0, 1);
		int count = 1;

		for (int i = 1; i < input.length(); i++) {

			String digit = input.substring(i, i + 1);

			if (currentDigit.equals(digit)) {
				count++;
			} else {
				output.append(count + currentDigit);
				currentDigit = digit;
				count = 1;
			}
		}

		output.append(count + currentDigit);

		return output.toString();
	}

	public static String lookAndSay(String input, int times) {
		String value = input;

		for (int i = 0; i < times; i++) {
			value = lookAndSay(value);
		}

		return value;
	}

	public static void main(String[] args) {
		System.out.println(lookAndSay("1321131112", 50).length());
	}

}
