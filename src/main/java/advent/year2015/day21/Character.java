package advent.year2015.day21;

public class Character {
	private int hitPoints;
	private int armor;
	private int damage;

	public Character(int hitPoints, int armor, int damage) {
		this.hitPoints = hitPoints;
		this.armor = armor;
		this.damage = damage;
	}

	public boolean isAlive() {
		return this.hitPoints > 0;
	}

	public int getHitPoints() {
		return this.hitPoints;
	}

	public int damageDealtTo(Character target) {
		return Math.max(1, this.damage - target.armor);
	}

	public static final int HERO_HP = 100;

	public static Character boss() {
		return new Character(100, 2, 8);
	}

	@Override
	public String toString() {
		return "Character [hitPoints=" + this.hitPoints + ", armor=" + this.armor + ", damage=" + this.damage + "]";
	}
}
