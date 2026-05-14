package app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The draw pile (mazo) of a Chinchón game. A deck contains all 40 cards
 * of the Spanish deck. The deck can be
 * refilled from the discard pile up to a maximum number of times per round.
 */
public class Deck {

	private List<Card> cards = new ArrayList<Card>();

	public Deck() {
		for (Suit suit : Suit.values()) {
			for (Rank rank : Rank.values()) {
				cards.add(new Card(suit, rank));
			}
		}
	}

	/**
	 * Shuffles all cards in the deck into a random order.
	 */
	public void shuffle() {
		Collections.shuffle(cards);
	}

	/**
	 * Returns the top card without removing it.
	 *
	 * @return the card on top of the deck
	 */
	public Card getTopCard() {
		return cards.getLast();
	}

	/**
	 * Removes and returns the top card of the deck.
	 *
	 * @return the drawn card, or null if the deck is empty
	 */
	public Card draw() {
		if (cards.isEmpty())
			return null;
		return cards.removeLast();
	}

	public boolean isEmpty() {
		return cards.isEmpty();
	}

	/**
	 * Refills the deck using all cards from the discard pile except its top card.
	 * The recycled cards are shuffled before being added.
	 *
	 * @param discardPile the discard pile to draw cards from
	 */
	public void refill(DiscardPile discardPile) {
		List<Card> recycled;
		recycled = discardPile.takeAllButTop();
		Collections.shuffle(recycled);
		cards.addAll(recycled);
	}
}
