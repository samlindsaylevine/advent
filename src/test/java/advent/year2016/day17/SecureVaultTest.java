package advent.year2016.day17;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Test;

import advent.year2016.day17.SecureVault.Path;

public class SecureVaultTest {

	@Test
	public void hijkl() {
		assertEquals(Optional.empty(), new SecureVault("hijkl").shortestPathToExit());
	}

	@Test
	public void ihgpwlah() {
		assertEquals(Optional.of("DDRRRD"), //
				new SecureVault("ihgpwlah").shortestPathToExit().map(Path::toString));
	}

	@Test
	public void kglvqrro() {
		assertEquals(Optional.of("DDUDRLRRUDRD"), //
				new SecureVault("kglvqrro").shortestPathToExit().map(Path::toString));
	}

	@Test
	public void ulqzkmiv() {
		assertEquals(Optional.of("DRURDRUDDLLDLUURRDULRLDUUDDDRR"), //
				new SecureVault("ulqzkmiv").shortestPathToExit().map(Path::toString));
	}

	@Test
	public void longestIhgpwlah() {
		assertEquals(Optional.of(370), //
				new SecureVault("ihgpwlah").longestPathToExitLength());

	}

	@Test
	public void longestKglvqrro() {
		assertEquals(Optional.of(492), //
				new SecureVault("kglvqrro").longestPathToExitLength());
	}

	@Test
	public void longestUlqzkmiv() {
		assertEquals(Optional.of(830), //
				new SecureVault("ulqzkmiv").longestPathToExitLength());
	}
}
