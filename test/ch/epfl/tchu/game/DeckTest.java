package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeckTest {

    SortedBag<Card> cards = SortedBag.of(
            List.of(Card.BLUE, Card.RED, Card.LOCOMOTIVE, Card.BLUE)
    );

    SortedBag<Card> emptyBag = SortedBag.of();

    Deck<Card> deck = Deck.of(cards, new Random());

    Deck<Card> emptyDeck = Deck.of(emptyBag, new Random());

    @Test
    void ofWorks() {
    }

    @Test
    void sizeWorks() {
        assertEquals(4, deck.size());
    }

    @Test
    void isEmptyOnEmpty() {
        assertEquals(true, emptyDeck.isEmpty());
    }

    @Test
    void isEmptyOnNonEmpty() {
        assertEquals(false, deck.isEmpty());
    }

    @Test
    void topCardWorks() {
    }

    @Test
    void topCardFailsOnEmptyDeck() {
        assertThrows(IllegalArgumentException.class, () ->
            emptyDeck.topCard()
        );
    }

    @Test
    void withoutTopCardWorks() {
    }

    @Test
    void withoutTopCardFailsOnEmptyDeck() {
        assertThrows(IllegalArgumentException.class, () ->
                emptyDeck.withoutTopCard()
        );
    }

    @Test
    void topCardsWorks() {
    }

    @Test
    void topCardsFails() {
        assertThrows(IllegalArgumentException.class, () ->
                deck.topCards(-1)
        );
        assertThrows(IllegalArgumentException.class, () ->
                deck.topCards(deck.size() + 1)
        );
    }

    @Test
    void withoutTopCardsWorks() {
    }

    @Test
    void withoutTopCardsFails() {
        assertThrows(IllegalArgumentException.class, () ->
                deck.topCards(-1)
        );
        assertThrows(IllegalArgumentException.class, () ->
                deck.topCards(deck.size() + 1)
        );
    }
}