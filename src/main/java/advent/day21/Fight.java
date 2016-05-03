package advent.day21;

public class Fight {

	private final Character firstStriker;
	private final Character secondStriker;

	private static void debug(String statement) {
		// System.out.println(statement);
	}

	public Fight(Character firstStriker, Character secondStriker) {
		this.firstStriker = firstStriker;
		this.secondStriker = secondStriker;

		debug(firstStriker + " vs " + secondStriker);
	}

	public Character winner() {
		int firstToSecond = this.firstStriker.damageDealtTo(this.secondStriker);
		debug("firstToSecond " + firstToSecond);
		int secondToFirst = this.secondStriker.damageDealtTo(this.firstStriker);
		debug("secondToFirst " + secondToFirst);

		int roundsSecondCharacterLives = roundUpDivision(this.secondStriker.getHitPoints(), firstToSecond);
		debug("roundsSecondCharacterLives " + roundsSecondCharacterLives);
		int roundsFirstCharacterLives = roundUpDivision(this.firstStriker.getHitPoints(), secondToFirst);
		debug("roundsFirstCharacterLives " + roundsFirstCharacterLives);

		if (roundsSecondCharacterLives > roundsFirstCharacterLives) {
			debug("Winner " + this.secondStriker);
			return this.secondStriker;
		} else {
			debug("Winner " + this.firstStriker);
			return this.firstStriker;
		}
	}

	static int roundUpDivision(int num, int divisor) {
		return (num + divisor - 1) / divisor;
	}

}
