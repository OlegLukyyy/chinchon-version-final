package app;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all players in a Chinchón game. Manages the player's
 * hand, accumulated score, and elimination status. Concrete subclasses
 * implement the turn logic via takeTurn.
 */
public abstract class Player {

	protected List<Card> hand;

	private String name;
	private int points;
	private boolean isEliminated;

	/**
	 * Adds a card to the player's hand.
	 *
	 * @param card the card to add
	 * @return true if the card was successfully added
	 */
	public abstract boolean takeCard(Card card);

	/**
	 * Removes and returns the card at the given position in the hand.
	 *
	 * @param index zero-based position of the card to discard
	 * @return the removed card
	 */
	public abstract Card discardCard(int index);

	/** Reserved for future use. */
	public abstract void stopRequest();

	/**
	 * Executes the player's full turn: drawing a card and either closing the round
	 * or discarding a card.
	 *
	 * @param deck      the draw pile
	 * @param pile      the discard pile
	 * @param roundTurn 1-based turn number within the current round
	 * @return true if the player closed the round this turn
	 */
	public abstract boolean takeTurn(Deck deck, DiscardPile pile, int roundTurn);

	/**
	 * Creates a player with the given name, an empty hand and zero points.
	 *
	 * @param name display name of the player
	 */
	public Player(String name) {
		this.hand = new ArrayList<Card>();
		this.name = name;
		this.points = 0;
		this.isEliminated = false;
	}

	/**
	 * Returns the player's current hand.
	 *
	 * @return list of cards in hand
	 */
	public List<Card> getHand() {
		return hand;
	}

	/**
	 * Returns the player's display name.
	 *
	 * @return name string
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the player's total accumulated points across all rounds. May be
	 * negative if the close-with-7 bonus has been applied.
	 *
	 * @return total score
	 */
	public int getPoints() {
		return points;
	}

	/**
	 * Adds points to the player's accumulated total. Pass a negative value to
	 * subtract (e.g. the -10 close bonus).
	 *
	 * @param points delta to apply
	 */
	public void winPoints(int points) {
		this.points += points;
	}

	/**
	 * Returns whether this player has been eliminated from the game.
	 *
	 * @return true if the player reached or exceeded the point limit
	 */
	public boolean isEliminated() {
		return isEliminated;
	}

	/**
	 * Marks this player as eliminated.
	 */
	public void eliminate() {
		isEliminated = true;
	}

	/**
	 * Removes all cards from the player's hand. Called at the start of each new
	 * round before dealing fresh cards.
	 */
	public void clearHand() {
		hand.clear();
	}

	/**
	 * Returns the player's hand as an array of card description strings.
	 *
	 * @return array where each element is the result of Card.toString()
	 */
	public String[] getHandAsStringArray() {
		return hand.stream().map(Card::toString).toArray(String[]::new);
	}

	/**
	 * Returns the player's hand as a numbered string for inline display.
	 *
	 * @return formatted string listing all cards with 1-based indices
	 */
	public String handToString() {
		StringBuilder sb;
		sb = new StringBuilder();
		for (int i = 0; i < hand.size(); i++) {
			sb.append(String.format("[%d] %s  ", i + 1, hand.get(i)));
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return "Player [hand=" + hand + ", name=" + name + ", points=" + points + "]";
	}
}
