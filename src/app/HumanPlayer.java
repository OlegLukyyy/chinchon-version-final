package app;

import java.util.ArrayList;
import java.util.List;

import tools.ConsoleInput;
import tools.ConsoleOutput;

/**
 * A human-controlled player in a Chinchón game. Prompts the user via
 * ConsoleInput to draw, optionally close, and discard. All input is validated
 * before being accepted.
 */
public class HumanPlayer extends Player {

	private static final int POINT_LIMIT = 100;

	/**
	 * Creates a new human player with the given display name.
	 *
	 * @param name the player's name
	 */
	public HumanPlayer(String name) {
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
	 * Runs the human player's interactive turn: draw, optionally close, discard.
	 * Closing is offered only when the hand satisfies the close conditions.
	 *
	 * @param deck      the draw pile
	 * @param pile      the discard pile
	 * @param roundTurn 1-based turn number; closing is only allowed from turn 2
	 *                  onward
	 * @return true if the player closed the round
	 */
	@Override
	public boolean takeTurn(Deck deck, DiscardPile pile, int roundTurn) {
		ConsoleInput ci;
		int selection;
		boolean wantsToClose;
		int cardIndex;
		ci = ConsoleInput.getInstance();
		ConsoleOutput.printHand(getName(), getHandAsStringArray());
		ConsoleOutput.printDiscardTop(pile.peek().toString());
		selection = ci.readInt("¿Coger descarte (1) o robar del mazo (2)? ", 1, 2);
		if (selection == 1) {
			takeCard(pile.takeTop());
		} else {
			takeCard(deck.draw());
		}
		ConsoleOutput.printHand(getName(), getHandAsStringArray());
		if (HandAnalyzer.canClose(hand, roundTurn, getPoints(), POINT_LIMIT)) {
			wantsToClose = ci.readConfirmation("¿Quieres cerrar?");
			if (wantsToClose) {
				return executeClose(pile, ci);
			}
		}
		cardIndex = ci.readInt("Elige una carta para descartar (1-" + hand.size() + "): ", 1, hand.size());
		pile.add(discardCard(cardIndex - 1));
		return false;
	}

	/**
	 * Asks the player which card to discard when closing and validates the choice.
	 * Repeats until the resulting 7-card hand satisfies the close conditions.
	 *
	 * @param pile the discard pile to receive the discarded card
	 * @param ci   the console input instance
	 * @return always true
	 */
	private boolean executeClose(DiscardPile pile, ConsoleInput ci) {
		boolean validSelection;
		int idx;
		List<Card> remaining;
		validSelection = false;
		while (!validSelection) {
			idx = ci.readInt("Elige carta para descartar al cerrar (1-" + hand.size() + "): ", 1, hand.size()) - 1;
			remaining = new ArrayList<>(hand);
			remaining.remove(idx);
			if (HandAnalyzer.isValidCloseHand(remaining)) {
				pile.add(discardCard(idx));
				validSelection = true;
			} else {
				ConsoleOutput.printError("Esa carta no permite cerrar. Elige otra.");
			}
		}
		return true;
	}
}
