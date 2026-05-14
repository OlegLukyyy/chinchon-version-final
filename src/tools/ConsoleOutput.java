package tools;

/**
 * Utility class for handling all console output in the Chinchón game.
 * Centralizes formatting so that changing the display only requires editing
 * this class. All methods are static — no instantiation needed.
 */
public class ConsoleOutput {

	private static final int LINE_WIDTH = 60;
	private static final char SEPARATOR = '-';
	private static final char HEADER_CHAR = '=';

	/**
	 * Prints a plain message followed by a newline.
	 */
	public static void println(String message) {
		System.out.println(message);
	}

	/**
	 * Prints a plain message without a trailing newline.
	 */
	public static void print(String message) {
		System.out.print(message);
	}

	/**
	 * Prints a blank line.
	 */
	public static void blank() {
		System.out.println();
	}

	/**
	 * Prints a full-width separator line made of dashes.
	 */
	public static void separator() {
		System.out.println(String.valueOf(SEPARATOR).repeat(LINE_WIDTH));
	}

	/**
	 * Prints a section header centered between '=' characters.
	 *
	 * @param title Text to display as the header.
	 */
	public static void header(String title) {
		String border;
		border = String.valueOf(HEADER_CHAR).repeat(LINE_WIDTH);
		System.out.println(border);
		System.out.println(center(title));
		System.out.println(border);
	}

	/**
	 * Prints a labeled value pair (e.g., "Score: 25").
	 *
	 * @param label Label text.
	 * @param value Value associated with the label.
	 */
	public static void printField(String label, Object value) {
		System.out.printf("  %-20s %s%n", label + ":", value);
	}

	/**
	 * Announces whose turn it currently is.
	 *
	 * @param playerName Name of the active player.
	 */
	public static void printTurn(String playerName) {
		blank();
		separator();
		System.out.printf("  TURN: %s%n", playerName);
		separator();
	}

	/**
	 * Displays a player's hand as a numbered list.
	 *
	 * @param playerName Name of the player.
	 * @param hand       Array of card descriptions (e.g., "7 of Spades").
	 */
	public static void printHand(String playerName, String[] hand) {
		System.out.printf("%n  %s's hand:%n", playerName);
		for (int i = 0; i < hand.length; i++) {
			System.out.printf("    [%d] %s%n", i + 1, hand[i]);
		}
	}

	/**
	 * Shows the top card of the discard pile.
	 *
	 * @param cardDescription String representation of the top discard card.
	 */
	public static void printDiscardTop(String cardDescription) {
		System.out.printf("  Carta descartada: %s%n", cardDescription);
	}

	/**
	 * Announces that a player has closed the round.
	 *
	 * @param playerName Name of the player who closed.
	 */
	public static void printRoundClosed(String playerName) {
		blank();
		header("ROUND CLOSED BY " + playerName.toUpperCase());
	}

	/**
	 * Prints the score of a single player.
	 *
	 * @param playerName Name of the player.
	 * @param score      Current total score.
	 */
	public static void printScore(String playerName, int score) {
		System.out.printf("  %-20s %d pts%n", playerName, score);
	}

	/**
	 * Announces the winner of the game.
	 *
	 * @param playerName Name of the winning player.
	 */
	public static void printWinner(String playerName) {
		blank();
		header("GANADOR: " + playerName.toUpperCase());
	}

	/**
	 * Prints name, points earned this round, and accumulated total.
	 */
	public static void printRoundScore(String playerName, int roundPoints, int total) {
		String sign;
		sign = roundPoints >= 0 ? "+" : "";
		System.out.printf("  %-20s %s%d pts  (total: %d)%n", playerName, sign, roundPoints, total);
	}

	/**
	 * Announces that a player has been eliminated.
	 */
	public static void printEliminated(String playerName, int points) {
		blank();
		System.out.printf("  💀 %s ha sido eliminado con %d puntos.%n", playerName, points);
	}

	/**
	 * Prints the scoreboard header.
	 */
	public static void printScoreboardHeader() {
		blank();
		header("PUNTUACIÓN DE LA RONDA");
	}

	/**
	 * Prints a deck-refill notification.
	 */
	public static void printDeckRefill(int count, int max) {
		println("  El mazo se ha agotado. Reinicio " + count + "/" + max + ".");
	}

	/**
	 * Prints the round-ended-without-close notification.
	 */
	public static void printRoundEndedNoClose() {
		blank();
		println("  El mazo se ha agotado sin reinicio disponible. Nadie puntúa esta ronda.");
	}

	/**
	 * Prints a general error or warning message.
	 *
	 * @param message The error text to display.
	 */
	public static void printError(String message) {
		System.out.println("  [!] " + message);
	}

	// -------------------------------------------------------------------------
	// Private helpers
	// -------------------------------------------------------------------------

	/**
	 * Centers a string within LINE_WIDTH using spaces.
	 */
	private static String center(String text) {
		int padding;
		if (text.length() >= LINE_WIDTH)
			return text;
		padding = (LINE_WIDTH - text.length()) / 2;
		return " ".repeat(padding) + text;
	}
}