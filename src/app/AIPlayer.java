package app;

import java.util.ArrayList;
import java.util.List;

import tools.ConsoleOutput;

/**
 * A computer-controlled player in a Chinchón game. Uses a greedy strategy:
 * draws the discard pile's top card only if it increases combined cards,
 * discards the highest-value unmatched card, and closes the round as soon as
 * the conditions are met.
 */
public class AIPlayer extends Player {

	private static final int POINT_LIMIT = 100;

	/**
	 * Creates a new AI player with the given display name.
	 *
	 * @param name the player's name
	 */
	public AIPlayer(String name) {
		super(name);
	}

	@Override
	public boolean takeCard(Card card) {
		return hand.add(card);
	}

	@Override
	public Card discardCard(int index) {
		return hand.remove(index);
	}

	@Override
	public void stopRequest() {
	}

	/**
	 * Draws the best available card, closes if possible, or discards the worst
	 * unmatched card.
	 *
	 * @param deck      the draw pile
	 * @param pile      the discard pile
	 * @param roundTurn 1-based turn number within the round
	 * @return true if the AI closed the round
	 */
	@Override
	public boolean takeTurn(Deck deck, DiscardPile pile, int roundTurn) {
		drawBestCard(deck, pile);

		if (HandAnalyzer.canClose(hand, roundTurn, getPoints(), POINT_LIMIT)) {
			return executeClose(pile);
		}

		discardWorstCard(pile);
		return false;
	}

	/**
	 * Takes the discard pile's top card if it increases combined cards; otherwise
	 * draws from the deck.
	 *
	 * @param deck the draw pile
	 * @param pile the discard pile
	 */
	private void drawBestCard(Deck deck, DiscardPile pile) {
		Card topDiscard;
		List<Card> handWithDiscard;
		Card drawn;
		int currentCombined;
		int combinedWithDiscard;
		topDiscard = pile.peek();
		currentCombined = HandAnalyzer.combinedCountForHand(hand);
		handWithDiscard = new ArrayList<>(hand);
		handWithDiscard.add(topDiscard);
		combinedWithDiscard = HandAnalyzer.combinedCountForHand(handWithDiscard);
		if (combinedWithDiscard > currentCombined) {
			takeCard(pile.takeTop());
			ConsoleOutput.println(getName() + " roba del descarte: " + topDiscard);
		} else {
			drawn = deck.draw();
			takeCard(drawn);
			ConsoleOutput.println(getName() + " roba del mazo.");
		}
	}

	/**
	 * Closes the round by discarding the card that leaves the lowest unmatched
	 * point total in the resulting 7-card hand.
	 *
	 * @param pile the discard pile to receive the discarded card
	 * @return always true
	 */
	private boolean executeClose(DiscardPile pile) {
		Card toDiscard;
		int idx;
		toDiscard = HandAnalyzer.getBestDiscardForClose(hand);
		idx = hand.indexOf(toDiscard);
		pile.add(discardCard(idx));
		ConsoleOutput.println(getName() + " CIERRA. Descarta: " + toDiscard);
		return true;
	}

	/**
	 * Discards the highest-value unmatched card, or the lowest-value card from the
	 * first group if all cards are combined.
	 *
	 * @param pile the discard pile to receive the discarded card
	 */
	private void discardWorstCard(DiscardPile pile) {
		List<List<Card>> combinations;
		List<Card> unmatched;
		Card toDiscard;
		int idx;
		combinations = HandAnalyzer.findBestCombinations(hand);
		unmatched = HandAnalyzer.getUnmatchedCards(hand, combinations);
		if (!unmatched.isEmpty()) {
			toDiscard = findHighestValue(unmatched);
		} else {
			toDiscard = findLowestValue(combinations.get(0));
		}
		idx = hand.indexOf(toDiscard);
		pile.add(discardCard(idx));
		ConsoleOutput.println(getName() + " descarta: " + toDiscard);
	}

	/**
	 * Returns the card with the highest point value from the given list.
	 *
	 * @param cards non-empty list of cards
	 * @return card with the maximum value
	 */
	private Card findHighestValue(List<Card> cards) {
		Card highest;
		highest = cards.get(0);
		for (Card card : cards) {
			if (card.getRank().getValue() > highest.getRank().getValue())
				highest = card;
		}
		return highest;
	}

	/**
	 * Returns the card with the lowest point value from the given list.
	 *
	 * @param cards non-empty list of cards
	 * @return card with the minimum value
	 */
	private Card findLowestValue(List<Card> cards) {
		Card lowest;
		lowest = cards.get(0);
		for (Card card : cards) {
			if (card.getRank().getValue() < lowest.getRank().getValue())
				lowest = card;
		}
		return lowest;
	}

	@Override
	public String toString() {
		return "AIPlayer [name=" + getName() + ", points=" + getPoints() + ", hand=" + getHand() + "]";
	}
}
