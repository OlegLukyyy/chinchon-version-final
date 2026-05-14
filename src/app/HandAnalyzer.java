package app;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Stateless utility class that analyses a player's hand in Chinchón. All
 * methods are static; this class is never instantiated.
 *
 * Combination types: - Iguales: 3 or more cards of the same rank, any suit. -
 * Escalera: 3 or more cards of the same suit with consecutive rank ordinals
 * (order: 1-7, Sota, Caballo, Rey). - Chinchon: all 7 cards form a single
 * escalera of the same suit.
 */
public class HandAnalyzer {

	/**
	 * Returns whether the given 7-card hand is a chinchon.
	 *
	 * @param hand exactly 7 cards
	 * @return true if all cards share the same suit and form a consecutive run
	 */
	public static boolean isChinchon(List<Card> hand) {
		Suit suit;
		List<Card> sorted;
		boolean sameSuit;
		boolean consecutive;
		if (hand.size() != 7)
			return false;
		suit = hand.get(0).getSuit();
		sorted = new ArrayList<>(hand);
		sameSuit = true;
		for (Card card : hand) {
			if (card.getSuit() != suit)
				sameSuit = false;
		}
		if (!sameSuit)
			return false;
		sorted.sort((a, b) -> Integer.compare(a.getRank().ordinal(), b.getRank().ordinal()));
		consecutive = true;
		for (int i = 1; i < sorted.size(); i++) {
			if (sorted.get(i).getRank().ordinal() != sorted.get(i - 1).getRank().ordinal() + 1) {
				consecutive = false;
			}
		}
		return consecutive;
	}

	public static List<List<Card>> findBestCombinations(List<Card> hand) {
		List<List<Card>> candidates;
		candidates = new ArrayList<>();
		candidates.addAll(findGroups(hand));
		candidates.addAll(findRuns(hand));
		return findBest(candidates, 0, new ArrayList<>());
	}

	/**
	 * Returns the cards from hand that are not covered by any group.
	 *
	 * @param hand         the full hand
	 * @param combinations groups returned by findBestCombinations
	 * @return unmatched cards (may be empty)
	 */

	public static List<Card> getUnmatchedCards(List<Card> hand, List<List<Card>> combinations) {
		List<Card> matched;
		List<Card> unmatched;
		matched = new ArrayList<>();
		for (List<Card> group : combinations) {
			matched.addAll(group);
		}
		unmatched = new ArrayList<>();
		for (Card card : hand) {
			if (!matched.contains(card))
				unmatched.add(card);
		}
		return unmatched;
	}

	/**
	 * Returns the total number of cards covered by the given groups.
	 *
	 * @param combinations list of groups
	 * @return sum of all group sizes
	 */
	public static int totalCombinedCards(List<List<Card>> combinations) {
		return countCards(combinations);
	}

	/**
	 * Returns whether the player may close the round this turn. Requires roundTurn
	 * >= 2, accumulatedPoints below the limit, and at least one valid discard that
	 * leaves a closeable 7-card hand. The hand is expected to have 8 cards (7
	 * original + 1 just taken).
	 *
	 * @param hand              the player's 8-card hand
	 * @param roundTurn         1-based turn number within the current round
	 * @param accumulatedPoints the player's score before this round
	 * @param pointLimit        the elimination threshold
	 * @return true if the player is allowed to close
	 */
	public static boolean canClose(List<Card> hand, int roundTurn, int accumulatedPoints, int pointLimit) {
		List<Card> remaining;
		if (roundTurn < 2 || accumulatedPoints >= pointLimit)
			return false;
		for (Card card : hand) {
			remaining = new ArrayList<>(hand);
			remaining.remove(card);
			if (isValidCloseHand(remaining))
				return true;
		}
		return false;
	}

	/**
	 * Returns whether the given 7-card hand satisfies the close conditions: all 7
	 * cards combined, or 6 combined with the single unmatched card valued at 5 or
	 * less.
	 *
	 * @param sevenCards exactly 7 cards
	 * @return true if the hand allows closing
	 */
	public static boolean isValidCloseHand(List<Card> sevenCards) {
		List<List<Card>> combinations;
		List<Card> unmatched;
		int combined;
		if (sevenCards.size() != 7)
			return false;
		combinations = findBestCombinations(sevenCards);
		combined = countCards(combinations);
		if (combined == 7)
			return true;
		if (combined == 6) {
			unmatched = getUnmatchedCards(sevenCards, combinations);
			return !unmatched.isEmpty() && unmatched.get(0).getRank().getValue() <= 5;
		}
		return false;
	}

	/**
	 * Returns the card to discard when closing that minimises the unmatched point
	 * total in the remaining 7-card hand.
	 *
	 * @param hand the player's 8-card hand
	 * @return the best card to discard, or null if no valid close discard exists
	 */
	public static Card getBestDiscardForClose(List<Card> hand) {
		Card bestDiscard;
		List<Card> remaining;
		List<List<Card>> combinations;
		List<Card> unmatched;
		int minPoints;
		int points;
		bestDiscard = null;
		minPoints = Integer.MAX_VALUE;
		for (Card card : hand) {
			remaining = new ArrayList<>(hand);
			remaining.remove(card);
			if (isValidCloseHand(remaining)) {
				combinations = findBestCombinations(remaining);
				unmatched = getUnmatchedCards(remaining, combinations);
				points = 0;
				for (Card u : unmatched)
					points += u.getRank().getValue();
				if (bestDiscard == null || points < minPoints) {
					minPoints = points;
					bestDiscard = card;
				}
			}
		}
		return bestDiscard;
	}

	/**
	 * Returns the number of combined cards in the given hand without requiring an
	 * intermediate call to findBestCombinations.
	 *
	 * @param hand any list of cards
	 * @return number of cards covered by the best combination partition
	 */
	public static int combinedCountForHand(List<Card> hand) {
		return countCards(findBestCombinations(hand));
	}

	/**
	 * Finds all iguales groups in the hand (3 or more cards of the same rank).
	 *
	 * @param hand the cards to search
	 * @return valid iguales groups
	 */
	static List<List<Card>> findGroups(List<Card> hand) {
		Map<Rank, List<Card>> byRank;
		List<List<Card>> groups;
		byRank = new LinkedHashMap<>();
		for (Card card : hand) {
			byRank.computeIfAbsent(card.getRank(), k -> new ArrayList<>()).add(card);
		}
		groups = new ArrayList<>();
		for (List<Card> group : byRank.values()) {
			if (group.size() >= 3)
				groups.add(new ArrayList<>(group));
		}
		return groups;
	}

	/**
	 * Finds all escalera groups in the hand (3 or more consecutive cards of the
	 * same suit). All valid sub-runs of a maximal sequence are included.
	 *
	 * @param hand the cards to search
	 * @return valid escalera groups
	 */
	static List<List<Card>> findRuns(List<Card> hand) {
		Map<Suit, List<Card>> bySuit;
		List<List<Card>> runs;
		List<Card> sorted;
		bySuit = new LinkedHashMap<>();
		for (Card card : hand) {
			bySuit.computeIfAbsent(card.getSuit(), k -> new ArrayList<>()).add(card);
		}
		runs = new ArrayList<>();
		for (List<Card> suitCards : bySuit.values()) {
			sorted = new ArrayList<>(suitCards);
			sorted.sort((a, b) -> Integer.compare(a.getRank().ordinal(), b.getRank().ordinal()));
			extractRuns(sorted, runs);
		}
		return runs;
	}

	/**
	 * Extracts all escalera sub-sequences of length >= 3 from a suit-sorted list
	 * and appends them to runs.
	 *
	 * @param sorted cards of one suit sorted by rank ordinal
	 * @param runs   output list to add discovered escaleras to
	 */
	private static void extractRuns(List<Card> sorted, List<List<Card>> runs) {
		List<List<Card>> sequences;
		List<Card> current;
		sequences = new ArrayList<>();
		current = new ArrayList<>();
		current.add(sorted.get(0));
		for (int i = 1; i < sorted.size(); i++) {
			if (sorted.get(i).getRank().ordinal() == sorted.get(i - 1).getRank().ordinal() + 1) {
				current.add(sorted.get(i));
			} else {
				if (current.size() >= 3)
					sequences.add(new ArrayList<>(current));
				current = new ArrayList<>();
				current.add(sorted.get(i));
			}
		}
		if (current.size() >= 3)
			sequences.add(new ArrayList<>(current));
		for (List<Card> seq : sequences) {
			for (int start = 0; start <= seq.size() - 3; start++) {
				for (int end = start + 3; end <= seq.size(); end++) {
					runs.add(new ArrayList<>(seq.subList(start, end)));
				}
			}
		}
	}

	/**
	 * Recursively finds the non-overlapping subset of candidates that covers the
	 * most cards, using include/exclude branching on each candidate.
	 *
	 * @param candidates all possible valid groups
	 * @param idx        current index into candidates
	 * @param chosen     groups selected so far in this branch
	 * @return the best selection found from index idx onward
	 */
	private static List<List<Card>> findBest(List<List<Card>> candidates, int idx, List<List<Card>> chosen) {
		List<Card> candidate;
		List<List<Card>> withSkip;
		List<List<Card>> withInclude;
		if (idx == candidates.size())
			return new ArrayList<>(chosen);
		candidate = candidates.get(idx);
		withSkip = findBest(candidates, idx + 1, chosen);
		if (!noOverlap(candidate, chosen))
			return withSkip;
		chosen.add(candidate);
		withInclude = findBest(candidates, idx + 1, chosen);
		chosen.remove(chosen.size() - 1);
		if (countCards(withInclude) > countCards(withSkip))
			return withInclude;
		return withSkip;
	}

	/**
	 * Returns whether candidate shares no card references with any chosen group.
	 *
	 * @param candidate group to test
	 * @param chosen    already-selected groups
	 * @return true if there is no card in common
	 */
	private static boolean noOverlap(List<Card> candidate, List<List<Card>> chosen) {
		for (List<Card> group : chosen) {
			for (Card card : candidate) {
				if (group.contains(card))
					return false;
			}
		}
		return true;
	}

	/**
	 * Sums the sizes of all groups.
	 *
	 * @param groups list of groups
	 * @return total card count
	 */
	private static int countCards(List<List<Card>> groups) {
		int count;
		count = 0;
		for (List<Card> group : groups)
			count += group.size();
		return count;
	}
}
