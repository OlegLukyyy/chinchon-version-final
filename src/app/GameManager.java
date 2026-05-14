package app;

import java.util.ArrayList;
import java.util.List;

import tools.ConsoleInput;
import tools.ConsoleOutput;

/**
 * Central controller for a Chinchón game session. Manages the full game
 * lifecycle: initial menus, multi-round loop, round deal, turn dispatch,
 * scoring, player elimination and victory detection.
 */
public class GameManager {

	private List<Player> players = new ArrayList<>();
	private ConsoleInput ci;

	/** Tracks how many times the deck has been refilled in the current round. */
	private int deckRefillCount;

	/**
	 * The player who closed the current round, or null if the round ended due to
	 * deck exhaustion.
	 */
	private Player roundCloser;

	/** True if the round was closed with a chinchón. */
	private boolean chinchonWon;

	private static final int POINT_LIMIT = 100;
	private static final int MAX_REFILLS = 2;
	private static final int HAND_SIZE = 7;

	public GameManager() {
		this.ci = ConsoleInput.getInstance();
	}

	/**
	 * Displays the main menu and waits for the player to choose between starting a
	 * new game or exiting.
	 */
	public void start() {
		int option;
		mainMenu();
		option = ci.readInt("Opción: ", 1, 2);
		switch (option) {
		case 1 -> newGame();
		case 2 -> stop();
		}
	}

	/**
	 * Clears the player list, runs the setup menu to create players, and starts the
	 * game loop.
	 */
	public void newGame() {
		players.clear();
		newGameMenu();
		playGame();
	}

	// Game loop

	/**
	 * Outer game loop: plays rounds until a winner is declared. After each round,
	 * scoring, eliminations and victory checks are performed.
	 */
	private void playGame() {
		boolean gameRunning;
		List<Player> active;
		gameRunning = true;
		while (gameRunning) {
			deckRefillCount = 0;
			roundCloser = null;
			chinchonWon = false;

			playRound();

			if (chinchonWon && roundCloser != null) {
				ConsoleOutput.printWinner(roundCloser.getName() + " — ¡CHINCHÓN!");
				gameRunning = false;
			} else {
				if (roundCloser != null) {
					processRoundScoring();
				} else {
					ConsoleOutput.printRoundEndedNoClose();
				}
				eliminatePlayers();
				active = getActivePlayers();
				if (active.size() <= 1) {
					announceWinner(active);
					gameRunning = false;
				}
			}
		}
	}

	/**
	 * Inner round loop: deals cards, then iterates player turns until a player
	 * closes or the deck cannot be refilled a third time. Sets oundCloser and
	 * chinchonWon before returning.
	 */
	private void playRound() {
		Deck deck;
		DiscardPile discardPile;
		List<Player> active;
		boolean closed;
		int roundTurn;
		boolean roundRunning;
		deck = new Deck();
		deck.shuffle();
		discardPile = new DiscardPile();

		for (Player player : getActivePlayers()) {
			player.clearHand();
			for (int i = 0; i < HAND_SIZE; i++) {
				player.takeCard(deck.draw());
			}
		}
		discardPile.add(deck.draw());

		roundTurn = 1;
		roundRunning = true;

		while (roundRunning) {
			active = getActivePlayers();
			for (Player player : active) {
				if (roundRunning) {
					if (deck.isEmpty()) {
						if (deckRefillCount < MAX_REFILLS) {
							deckRefillCount++;
							deck.refill(discardPile);
							ConsoleOutput.printDeckRefill(deckRefillCount, MAX_REFILLS);
						} else {
							roundRunning = false;
						}
					}
					if (roundRunning) {
						ConsoleOutput.printTurn(player.getName());
						closed = player.takeTurn(deck, discardPile, roundTurn);
						if (closed) {
							roundCloser = player;
							chinchonWon = HandAnalyzer.isChinchon(player.getHand());
							roundRunning = false;
						}
					}
				}
			}
			roundTurn++;
		}
	}

	/**
	 * Calculates and applies round scores for every active player. Each player's
	 * score is the sum of their unmatched card values. The player who closed with
	 * all 7 cards combined receives a −10 bonus. Scores are added to accumulated
	 * totals.
	 */
	private void processRoundScoring() {
		List<List<Card>> combinations;
		List<Card> unmatched;
		int roundPoints;
		boolean closedWithAll7;
		ConsoleOutput.printRoundClosed(roundCloser.getName());
		ConsoleOutput.printScoreboardHeader();

		for (Player player : getActivePlayers()) {
			combinations = HandAnalyzer.findBestCombinations(player.getHand());
			unmatched = HandAnalyzer.getUnmatchedCards(player.getHand(), combinations);

			roundPoints = 0;
			for (Card card : unmatched)
				roundPoints += card.getRank().getValue();

			closedWithAll7 = (player == roundCloser) && HandAnalyzer.totalCombinedCards(combinations) == HAND_SIZE;
			if (closedWithAll7)
				roundPoints -= 10;

			player.winPoints(roundPoints);
			ConsoleOutput.printRoundScore(player.getName(), roundPoints, player.getPoints());
		}
	}

	/**
	 * Checks every player's accumulated score and eliminates those who have reached
	 * or exceeded max points.
	 */
	private void eliminatePlayers() {
		for (Player player : players) {
			if (!player.isEliminated() && player.getPoints() >= POINT_LIMIT) {
				player.eliminate();
				ConsoleOutput.printEliminated(player.getName(), player.getPoints());
			}
		}
	}

	/**
	 * Announces the winner after the game ends by elimination. If exactly one
	 * active player remains, that player wins. If all players were eliminated
	 * simultaneously, the one with the lowest accumulated score is declared the
	 * winner.
	 *
	 * @param active players who were not eliminated at the time of the check
	 */
	private void announceWinner(List<Player> active) {
		Player lowest;
		if (active.size() == 1) {
			ConsoleOutput.printWinner(active.get(0).getName() + " — último en pie");
		} else {
			lowest = findLowestScore();
			ConsoleOutput.printWinner(lowest.getName() + " — menor puntuación");
		}
	}

	/**
	 * Returns the player with the lowest accumulated score across the players list.
	 *
	 * @return the player with the minimum points value
	 */
	private Player findLowestScore() {
		Player lowest;
		lowest = players.get(0);
		for (Player player : players) {
			if (player.getPoints() < lowest.getPoints())
				lowest = player;
		}
		return lowest;
	}

	/**
	 * Returns a new list containing only the players who have not been eliminated.
	 *
	 * @return list of active (non-eliminated) players in their original order
	 */
	private List<Player> getActivePlayers() {
		List<Player> active;
		active = new ArrayList<>();
		for (Player player : players) {
			if (!player.isEliminated())
				active.add(player);
		}
		return active;
	}

	/**
	 * Prints the game title banner to the console.
	 */
	public void showTitle() {
		ConsoleOutput.print("=============================================================\n"
				+ "                         CHINCHON\n"
				+ "=============================================================\n" + "Elige una opción:\n");
	}

	/**
	 * Displays the main menu with the title banner.
	 */
	public void mainMenu() {
		showTitle();
		ConsoleOutput.print("1. Partida Nueva\n" + "2. Salir\n");
	}

	/**
	 * Interactively builds the player list by asking the number of players and, for
	 * each one, whether they are human (h) or AI (i), and their name. Prints a
	 * summary of the created players.
	 */
	public void newGameMenu() {
		int numPlayers;
		String option;
		String name;
		showTitle();

		numPlayers = ci.readInt("Número de jugadores (2-5): ", 2, 5);

		for (int i = 1; i <= numPlayers; i++) {
			option = ci.readLine(String.format("¿El jugador %d es real o IA? (h/i): ", i));
			switch (option) {
			case "h", "H" -> {
				name = ci.readLine("Introduce el nombre del jugador: ");
				players.add(new HumanPlayer(name));
			}
			case "i", "I" -> {
				name = ci.readLine(String.format("Introduce el nombre del jugador %d: ", i));
				players.add(new AIPlayer(name));
			}
			default -> {
				ConsoleOutput.printError("Opción no válida. Se añade como jugador real.");
				name = ci.readLine("Introduce el nombre del jugador: ");
				players.add(new HumanPlayer(name));
			}
			}
		}

		ConsoleOutput.separator();
		for (Player player : players) {
			ConsoleOutput.println("Jugador: " + player.getName() + " - Tipo: " + player.getClass().getSimpleName());
		}
		ConsoleOutput.separator();
	}

	/**
	 * Prints the exit message and terminates the session.
	 */
	public void stop() {
		ConsoleOutput.print("Gracias por jugar a Chinchón. ¡Hasta la próxima!");
	}
}
