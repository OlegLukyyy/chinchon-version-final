package app;

/**
 * The ten ranks of the Spanish deck, excluding 8 and 9. Point values: 1-7 equal
 * their face number; Sota = 10, Caballo = 11, Rey = 12. The ordinal of each
 * constant reflects the natural playing order used when detecting consecutive
 * runs (escaleras).
 */
public enum Rank {

	UNO(1), DOS(2), TRES(3), CUATRO(4), CINCO(5), SEIS(6), SIETE(7), SOTA(10), CABALLO(11), REY(12);

	private final int value;

	Rank(int value) {
		this.value = value;
	}

	/**
	 * Returns the point value of this rank.
	 *
	 * @return numeric point value 
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Returns the rank name in title-case.
	 *
	 * @return human-readable rank name
	 */
	@Override
	public String toString() {
		return name().charAt(0) + name().substring(1).toLowerCase();
	}
}
