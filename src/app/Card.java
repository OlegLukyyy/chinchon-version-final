package app;

/**
 * A single card from the Spanish deck, defined by its suit and rank. Cards are
 * immutable once created.
 */
public class Card {

	private Suit suit;
	private Rank rank;

	/**
	 * Creates a new card with the given suit and rank.
	 *
	 * @param suit the card's suit
	 * @param rank the card's rank
	 */
	public Card(Suit suit, Rank rank) {
		this.suit = suit;
		this.rank = rank;
	}

	/**
	 * Returns the rank of this card.
	 *
	 * @return the card's rank
	 */
	public Rank getRank() {
		return rank;
	}

	/**
	 * Returns the suit of this card.
	 *
	 * @return the card's suit
	 */
	public Suit getSuit() {
		return suit;
	}

	@Override
	public String toString() {
		return String.format("%s[%s]", rank, suit);
	}
}
