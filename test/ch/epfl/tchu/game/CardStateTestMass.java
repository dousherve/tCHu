package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CardStateTestMass {
    CardState trivialCase = CardState.of(Deck.of(SortedBag.of(5,Card.WHITE,5,Card.BLUE), new Random()));

    @Test
    void constructorNoSuffCards() {
        assertThrows(IllegalArgumentException.class, () -> {
            CardState.of(Deck.of(SortedBag.of(2,Card.WHITE,2,Card.BLUE), new Random()));
        });
    }

    @Test
    void topCardDesk() {
        CardState emtptyCase = CardState.of(Deck.of(SortedBag.of(6,Card.WHITE), new Random()));
        CardState emtptyCaseL = CardState.of(Deck.of(SortedBag.of(6,Card.LOCOMOTIVE), new Random()));
        assertEquals(Card.WHITE, emtptyCase.topDeckCard());
        assertEquals(Card.LOCOMOTIVE, emtptyCaseL.topDeckCard());

    }

    @Test
    void emptyDeckState() {
        assertThrows(IllegalArgumentException.class, () -> {
            CardState.of(Deck.of(SortedBag.of(0,Card.WHITE,0,Card.BLUE), new Random())).topDeckCard();
        });
    }
    @Test
    void withoutTopCardEmptyCase() {
        assertThrows(IllegalArgumentException.class, () -> {
            CardState.of(Deck.of(SortedBag.of(0,Card.WHITE,0,Card.BLUE), new Random())).withoutTopDeckCard();
        });
    }
    @Test
    void withoutTopCardTrivialCase() {
        assertEquals(trivialCase.deckSize()-1, trivialCase.withoutTopDeckCard().deckSize());

    }
    @Test
    void newDeckWithDiscard() {
        CardState trivialCase = CardState.of(Deck.of(SortedBag.of(3,Card.WHITE,3,Card.BLUE), new Random()));
        CardState withDiscards = trivialCase.withMoreDiscardedCards(SortedBag.of(3,Card.WHITE,10,Card.BLUE));
        // CardState vide = withDiscards.withoutTopDeckCard().withoutTopDeckCard().withoutTopDeckCard().withoutTopDeckCard();
        CardState vide = withDiscards;
        assertThrows(IllegalArgumentException.class, () -> {
            vide.withDeckRecreatedFromDiscards(new Random());
        });
        assertEquals(13, vide.withoutTopDeckCard().withDeckRecreatedFromDiscards(new Random()).deckSize());

    }
}
