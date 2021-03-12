package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CardStateTest {

    SortedBag<Card> cards = SortedBag.of(
            List.of(Card.BLUE, Card.RED, Card.LOCOMOTIVE, Card.BLUE, Card.GREEN)
    );
    SortedBag<Card> cardsEnough = SortedBag.of(
            List.of(Card.BLUE, Card.RED, Card.LOCOMOTIVE, Card.BLUE, Card.GREEN, Card.GREEN, Card.RED, Card.VIOLET, Card.BLACK)
    );
    SortedBag<Card> cardsNotEnough = SortedBag.of(
            List.of(Card.BLUE, Card.RED, Card.LOCOMOTIVE, Card.BLUE)
    );
    SortedBag<Card> additional = SortedBag.of(
            List.of(Card.BLUE, Card.WHITE, Card.RED)
    );

    CardState cardStateWithDeck = CardState.of(Deck.of(cardsEnough, new Random()));
    CardState cardStateWithoutDeck = CardState.of(Deck.of(cards, new Random()));

    @Test
    void ofWorks() {
    }

    @Test
    void ofFails() {
        assertThrows(IllegalArgumentException.class, () ->
                CardState.of(Deck.of(cardsNotEnough, new Random()))
        );
    }

    @Test
    void withDrawnFaceUpCardWorks() {
    }

    @Test
    void withDrawnFaceUpCardFails() {
        assertThrows(IllegalArgumentException.class, () ->
                cardStateWithoutDeck.withDrawnFaceUpCard(1)
        );
        assertThrows(IndexOutOfBoundsException.class, () ->
                cardStateWithDeck.withDrawnFaceUpCard(-1)
        );
        assertThrows(IndexOutOfBoundsException.class, () ->
                cardStateWithDeck.withDrawnFaceUpCard(5)
        );
    }

    @Test
    void topDeckCardWorks() {
    }

    @Test
    void topDeckCardFails() {
        assertThrows(IllegalArgumentException.class, () ->
                cardStateWithoutDeck.topDeckCard()
        );
    }

    @Test
    void withoutTopDeckCardWorks() {
    }

    @Test
    void withoutTopDeckCardFails() {
        assertThrows(IllegalArgumentException.class, () ->
                cardStateWithoutDeck.withoutTopDeckCard()
        );
    }

    @Test
    void withDeckRecreatedFromDiscardsWorks() {
    }

    @Test
    void withDeckRecreatedFromDiscardsFails() {
        assertThrows(IllegalArgumentException.class, () ->
                cardStateWithDeck.withDeckRecreatedFromDiscards(new Random())
        );
    }

    @Test
    void withMoreDiscardedCardsWorks() {
        //assertEquals(, cardStateWithDeck.withMoreDiscardedCards(additional));
    }
}