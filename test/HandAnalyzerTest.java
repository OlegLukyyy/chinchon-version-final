package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import app.Card;
import app.HandAnalyzer;
import app.Rank;
import app.Suit;


public class HandAnalyzerTest {


	@ParameterizedTest
	@CsvSource({
		"1O 2O 3O 4O 5O 6O 7O, true",
		"4O 5O 6O 7O 10O 11O 12O, true",
		"1C 2C 3C 4C 5C 6C 7C, true",
		"1O 2O 3O 4O 5O 6O 7C, false",
		"1O 2O 3O 4O 5O 6O 11O, false",
		"1O 2O 3O 4O 5O 6O 1C, false"
	})
	void isChinchon_devuelveResultadoEsperado(String handSpec, boolean expected) {
		List<Card> hand;
		hand = buildHand(handSpec);
		assertEquals(expected, HandAnalyzer.isChinchon(hand));
	}


	@ParameterizedTest
	@ValueSource(ints = { 0, 1 })
	void canClose_enTurnoMenorDeDos_devuelveFalse(int roundTurn) {
		List<Card> hand;
		hand = buildHand("1O 2O 3O 4O 5O 6O 7O 1C");
		assertFalse(HandAnalyzer.canClose(hand, roundTurn, 0, 100));
	}


	@ParameterizedTest
	@CsvSource({
		"100, 100",
		"101, 100",
		"150, 100",
		"200, 100"
	})
	void canClose_conPuntosEnOSobreLimite_devuelveFalse(int accumulatedPoints, int pointLimit) {
		List<Card> hand;
		hand = buildHand("1O 2O 3O 4O 5O 6O 7O 1C");
		assertFalse(HandAnalyzer.canClose(hand, 2, accumulatedPoints, pointLimit));
	}


	@ParameterizedTest
	@CsvSource({
		"2, 0",
		"3, 50",
		"10, 99"
	})
	void canClose_conManoCerrableYCondicionesValidas_devuelveTrue(int roundTurn, int accumulatedPoints) {
		List<Card> hand;
		hand = buildHand("1O 2O 3O 4O 5O 6O 7O 1C");
		assertTrue(HandAnalyzer.canClose(hand, roundTurn, accumulatedPoints, 100));
	}

	@ParameterizedTest
	@CsvSource({
		"1O 2O 3O 4O 5O 6O 7O, true",
		"4O 5O 6O 7O 10O 11O 12O, true",
		"1O 2O 3O 1C 2C 3C 1E, true",
		"1O 2O 3O 4O 5O 6O 12O, false",
		"7O 11O 12O 7C 7E 4C 5C, false"
	})
	void isValidCloseHand_devuelveResultadoEsperado(String handSpec, boolean expected) {
		List<Card> hand;
		hand = buildHand(handSpec);
		assertEquals(expected, HandAnalyzer.isValidCloseHand(hand));
	}


	@ParameterizedTest
	@CsvSource({
		"1O 2O 3O 4C 7E 11B 12C, 3",
		"1O 2O 3O 1C 2C 3C 7E, 6",
		"1O 1C 1E 1B 2O 3O 4O, 7",
		"11O 11C 11E 12B 7O 6O 1C, 3"
	})
	void combinedCountForHand_devuelveNumeroEsperado(String handSpec, int expected) {
		List<Card> hand;
		hand = buildHand(handSpec);
		assertEquals(expected, HandAnalyzer.combinedCountForHand(hand));
	}


	// -------------------------------------------------------------------------
	// isChinchon — manos con exactamente 6 cartas
	// -------------------------------------------------------------------------

	@ParameterizedTest
	@ValueSource(strings = {
		"1O 2O 3O 4O 5O 6O",
		"1C 2C 3C 4C 5C 6C",
		"4E 5E 6E 7E 10E 11E"
	})
	void isChinchon_con6Cartas_devuelveFalse(String handSpec) {
		List<Card> hand;
		hand = buildHand(handSpec);
		assertFalse(HandAnalyzer.isChinchon(hand));
	}

	// -------------------------------------------------------------------------
	// isValidCloseHand — frontera del valor 5 en la carta suelta
	// -------------------------------------------------------------------------

	@ParameterizedTest
	@CsvSource({
		"1O 2O 3O 1C 2C 3C 1B, true",
		"1O 2O 3O 1C 2C 3C 4E, true",
		"1O 2O 3O 1C 2C 3C 5E, true",
		"1O 2O 3O 1C 2C 3C 6E, false",
		"1O 2O 3O 1C 2C 3C 7E, false",
		"1O 2O 3O 1C 2C 3C 10E, false"
	})
	void isValidCloseHand_conCartaSueltaEnFrontera_devuelveResultadoEsperado(String handSpec, boolean expected) {
		List<Card> hand;
		hand = buildHand(handSpec);
		assertEquals(expected, HandAnalyzer.isValidCloseHand(hand));
	}

	// -------------------------------------------------------------------------
	// getUnmatchedCards — número de cartas sin combinar
	// -------------------------------------------------------------------------

	@ParameterizedTest
	@CsvSource({
		"1O 2O 3O 4O 5O 6O 7O, 0",
		"1O 2O 3O 1C 2C 3C 1E, 1",
		"1O 2O 3O 4C 5E 6B 7O, 4",
		"1O 1C 1E 1B 7O 11C 12E, 3",
		"2O 4C 6E 1B 3O 5C 7E, 7"
	})
	void getUnmatchedCards_devuelveCantidadEsperada(String handSpec, int expectedUnmatched) {
		List<Card> hand;
		List<List<Card>> combinations;
		List<Card> unmatched;
		hand = buildHand(handSpec);
		combinations = HandAnalyzer.findBestCombinations(hand);
		unmatched = HandAnalyzer.getUnmatchedCards(hand, combinations);
		assertEquals(expectedUnmatched, unmatched.size());
	}

	// -------------------------------------------------------------------------
	// getBestDiscardForClose — valor de la carta que conviene descartar
	// -------------------------------------------------------------------------

	@ParameterizedTest
	@CsvSource({
		"1O 2O 3O 4O 5O 6O 7O 12C, 12",
		"1O 2O 3O 4O 5O 6O 7O 11C, 11",
		"1O 2O 3O 4O 5O 6O 7O 7C, 7",
		"1O 2O 3O 1C 2C 3C 1E 12B, 12"
	})
	void getBestDiscardForClose_descartaCartaConValorEsperado(String handSpec, int expectedValue) {
		List<Card> hand;
		Card bestDiscard;
		hand = buildHand(handSpec);
		bestDiscard = HandAnalyzer.getBestDiscardForClose(hand);
		assertEquals(expectedValue, bestDiscard.getRank().getValue());
	}

	// -------------------------------------------------------------------------
	// combinedCountForHand — iguales y combinaciones mixtas
	// -------------------------------------------------------------------------

	@ParameterizedTest
	@CsvSource({
		"1O 1C 1E 2O 2C 2E 3O, 6",
		"7O 7C 7E 7B 10O 10C 10E, 7",
		"1O 1C 1E 1B 5O 6O 7O, 7",
		"2O 4C 6E 1B 3O 5C 7E, 0"
	})
	void combinedCountForHand_conIgualesYEscaleras_devuelveNumeroEsperado(String handSpec, int expected) {
		List<Card> hand;
		hand = buildHand(handSpec);
		assertEquals(expected, HandAnalyzer.combinedCountForHand(hand));
	}

	// -------------------------------------------------------------------------
	// canClose — mano no cerrable (ningún descarte deja mano válida)
	// -------------------------------------------------------------------------

	@ParameterizedTest
	@ValueSource(strings = {
		"1O 4C 7E 11B 12C 2O 3C 6E",
		"2O 4C 6E 1B 3O 5C 7E 12O"
	})
	void canClose_conManoNoCerrable_devuelveFalse(String handSpec) {
		List<Card> hand;
		hand = buildHand(handSpec);
		assertFalse(HandAnalyzer.canClose(hand, 3, 0, 100));
	}

	private List<Card> buildHand(String spec) {
		List<Card> hand;
		String[] parts;
		String rankStr;
		char suitChar;
		int rankVal;
		hand = new ArrayList<>();
		parts = spec.split(" ");
		for (String part : parts) {
			rankStr = part.substring(0, part.length() - 1);
			suitChar = part.charAt(part.length() - 1);
			rankVal = Integer.parseInt(rankStr);
			hand.add(new Card(suitFromChar(suitChar), rankFromValue(rankVal)));
		}
		return hand;
	}

	private Rank rankFromValue(int value) {
		Rank result;
		result = null;
		for (Rank r : Rank.values()) {
			if (r.getValue() == value) {
				result = r;
			}
		}
		return result;
	}

	private Suit suitFromChar(char c) {
		Suit result;
		switch (c) {
		case 'O' -> result = Suit.OROS;
		case 'C' -> result = Suit.COPAS;
		case 'E' -> result = Suit.ESPADAS;
		default  -> result = Suit.BASTOS;
		}
		return result;
	}
}
