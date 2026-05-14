package app;

/**
 * The four suits of the Spanish deck: Oros, Copas, Espadas and Bastos.
 */
public enum Suit {

	OROS("🟡"), COPAS("🔴"), ESPADAS("⚔️"), BASTOS("🪵");

	private final String symbol;

	Suit(String symbol) {
		this.symbol = symbol;
	}

	/**
	 * Returns the emoji symbol associated with this suit.
	 *
	 * @return emoji string
	 */
	public String getSymbol() {
		return symbol;
	}

	/**
	 * Returns the suit name in title-case.
	 *
	 * @return human-readable suit name
	 */
	@Override
	public String toString() {
		return name().charAt(0) + name().substring(1).toLowerCase();
	}
}
