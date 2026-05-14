package tools;

import java.util.Scanner;

/**
 * Utility class for handling all console input in the Chinchón game. Wraps
 * Scanner to provide safe, validated input methods. Single instance (singleton)
 * to avoid multiple Scanner resources on System.in.
 */
public class ConsoleInput {

	private static ConsoleInput instance;
	private final Scanner scanner;

	private ConsoleInput() {
		this.scanner = new Scanner(System.in);
	}

	/**
	 * Returns the single shared instance of ConsoleInput.
	 */
	public static ConsoleInput getInstance() {
		if (instance == null) {
			instance = new ConsoleInput();
		}
		return instance;
	}

	/**
	 * Reads a non-empty line of text from the console.
	 *
	 * @param prompt Message shown to the user before reading.
	 * @return Trimmed, non-empty string entered by the user.
	 */
	public String readLine(String prompt) {
		String input;
		do {
			System.out.print(prompt);
			input = scanner.nextLine().trim();
			if (input.isEmpty()) {
				System.out.println("Input cannot be empty. Please try again.");
			}
		} while (input.isEmpty());
		return input;
	}

	/**
	 * Reads an integer within the given inclusive range [min, max].
	 *
	 * @param prompt Message shown to the user before reading.
	 * @param min    Minimum accepted value (inclusive).
	 * @param max    Maximum accepted value (inclusive).
	 * @return Valid integer entered by the user.
	 */
	public int readInt(String prompt, int min, int max) {
		int value;
		String input;
		boolean valid;
		value = 0;
		valid = false;
		while (!valid) {
			System.out.print(prompt);
			input = scanner.nextLine().trim();
			try {
				value = Integer.parseInt(input);
				if (value >= min && value <= max) {
					valid = true;
				} else {
					System.out.printf("Please enter a number between %d and %d.%n", min, max);
				}
			} catch (NumberFormatException e) {
				System.out.println("Invalid input. Please enter a whole number.");
			}
		}
		return value;
	}

	/**
	 * Reads a yes/no confirmation from the user. Accepts: y, yes, n, np
	 *
	 * @param prompt Message shown to the user before reading.
	 * @return true if the user confirmed (y/yes), false otherwise (n/no).
	 */
	public boolean readConfirmation(String prompt) {
		String input;
		boolean answered;
		boolean result;
		answered = false;
		result = false;
		while (!answered) {
			System.out.print(prompt + " (y/n): ");
			input = scanner.nextLine().trim().toLowerCase();
			switch (input) {
			case "y", "yes" -> {
				result = true;
				answered = true;
			}
			case "n", "no" -> {
				answered = true;
			}
			default -> System.out.println("Please enter 'y' or 'n'.");
			}
		}
		return result;
	}

	/**
	 * Waits for the user to press Enter before continuing.
	 *
	 * @param message Message shown before waiting.
	 */
	public void waitForEnter(String message) {
		System.out.print(message);
		scanner.nextLine();
	}

	/**
	 * Closes the underlying Scanner. Call only when the program is about to exit.
	 */
	public void close() {
		scanner.close();
	}
}