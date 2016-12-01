package advent.year2015.day8;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SantaString {

	private final String rawString;

	public SantaString(String rawString) {
		this.rawString = rawString;
	}

	public int rawLength() {
		return this.rawString.length();
	}

	public int decodedLength() {
		String unescaped = this.rawString;
		// Assuming all strings are double-quoted.
		unescaped = unescaped.substring(1, unescaped.length() - 1);
		unescaped = unescaped.replaceAll("\\\\x\\p{XDigit}{2}", ".");
		unescaped = unescaped.replace("\\\"", "\"");
		unescaped = unescaped.replace("\\\\", "\\");

		return unescaped.length();

	}

	public int encodedLength() {
		String encoded = this.rawString;
		encoded = encoded.replace("\\", "\\\\");
		encoded = encoded.replace("\"", "\\\"");
		encoded = "\"" + encoded + "\"";
		return encoded.length();
	}

	public static void main(String[] args) throws IOException {
		long total = Files.lines(Paths.get("src/main/java/advent/day8/input.txt")) //
				.map(SantaString::new) //
				.mapToInt(str -> str.encodedLength() - str.rawLength()) //
				.sum();

		System.out.println(total);
	}

}
