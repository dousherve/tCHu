package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PublicCardStateTest {

    List<Card> faceUpCards = List.of(
            Card.BLUE, Card.RED, Card.LOCOMOTIVE, Card.BLUE, Card.GREEN
    );

    List<Card> cardsNotEnough = List.of(
            Card.BLUE, Card.RED, Card.LOCOMOTIVE
    );

    List<Card> cardsTooMuch = List.of(
            Card.BLUE, Card.RED, Card.LOCOMOTIVE, Card.BLUE, Card.RED, Card.LOCOMOTIVE, Card.BLUE, Card.GREEN
    );

    PublicCardState pcs = new PublicCardState(faceUpCards, 25, 10);
    PublicCardState pcsWithEmptyDeck = new PublicCardState(faceUpCards, 0, 10);

    @Test
    void constructorFails() {
        assertThrows(IllegalArgumentException.class, () ->
                new PublicCardState(cardsNotEnough, 15, 25)
        );
        assertThrows(IllegalArgumentException.class, () ->
                new PublicCardState(cardsTooMuch, 15, 25)
        );
        assertThrows(IllegalArgumentException.class, () ->
                new PublicCardState(faceUpCards, -5, 25)
        );
        assertThrows(IllegalArgumentException.class, () ->
                new PublicCardState(faceUpCards, 10, -5)
        );
    }

    @Test
    void totalSizeWorks() {
        assertEquals(5 + 25 + 10, pcs.totalSize());
        assertEquals(5 + 0 + 10, pcsWithEmptyDeck.totalSize());
    }

    @Test
    void faceUpCardsWorks() {
        assertEquals(faceUpCards, pcs.faceUpCards());
    }

    @Test
    void faceUpCardWorks() {
        assertEquals(Card.BLUE, pcs.faceUpCard(0));
        assertEquals(Card.LOCOMOTIVE, pcs.faceUpCard(2));
        assertEquals(Card.GREEN, pcs.faceUpCard(4));
    }

    @Test
    void faceUpCardFails() {
        assertThrows(IndexOutOfBoundsException.class, () ->
            pcs.faceUpCard(-1)
        );
        assertThrows(IndexOutOfBoundsException.class, () ->
                pcs.faceUpCard(5)
        );
    }

    @Test
    void deckSizeWorks() {
        assertEquals(25, pcs.deckSize());
    }

    @Test
    void isDeckEmpty() {
        assertEquals(false, pcs.isDeckEmpty());
        assertEquals(true, pcsWithEmptyDeck.isDeckEmpty());
    }

    @Test
    void discardsSizeWorks() {
        assertEquals(10, pcs.discardsSize());
    }
    
}