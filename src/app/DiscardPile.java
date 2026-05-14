package app;

import java.util.ArrayList;
import java.util.List;

/**
 * The discard pile (montón de descarte) in a Chinchón game. Players may take
 * the top card instead of drawing from the deck. When the deck if off, all
 * cards except the current top are returned to the deck via takeAllButTop().
 */
public class DiscardPile {

	private List<Card> cards;


	public DiscardPile() {
		cards = new ArrayList<Card>();
	}

	/**
	 * Places a card on top of the discard pile.
	 *
	 * @param card the card to discard
	 */
	public void add(Card card) {
		cards.add(card);
	}

	/**
	 * Removes and returns the top card of the pile.
	 *
	 * @return the top card
	 */
	public Card takeTop() {
		return cards.removeLast();
	}

	/**
	 * Returns the top card without removing it.
	 *
	 * @return the current top card
	 */
	public Card peek() {
		return cards.getLast();
	}

	public boolean isEmpty() {
		return cards.isEmpty();
	}

	/**
	 * Removes and returns all cards from the pile except the current top card.
	 * Returns an empty list if the pile has one or fewer cards.
	 *
	 * @return list of recycled cards
	 */
	public List<Card> takeAllButTop() {
		if (cards.size() <= 1)
			return new ArrayList<>();
		List<Card> removed = new ArrayList<>(cards.subList(0, cards.size() - 1));
		cards.subList(0, cards.size() - 1).clear();
		return removed;
	}
}
