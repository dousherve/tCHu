package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PublicCardStateTestMass {
    PublicCardState trivialCase = new PublicCardState(List.of(Card.WHITE, Card.LOCOMOTIVE, Card.BLUE, Card.BLUE, Card.ORANGE),10,1);
    @Test
    void constructorFailsWithBigList() {
        List cardList = new ArrayList(Card.ALL);
        int a = 1;
        int b = 2;
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicCardState(cardList,a,b);
        });
    }
    @Test
    void constructorFailsWithNegative() {
        List cardList = new ArrayList(List.of(Card.WHITE, Card.LOCOMOTIVE, Card.BLUE, Card.BLUE, Card.ORANGE));
        int a = -1;
        int b = -2;
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicCardState(cardList,a,b);
        });
    }
    @Test
    void totalSize() {
        assertEquals(16, trivialCase.totalSize());
    }
    @Test
    void cardsFaceUp() {
        assertEquals(List.of(Card.WHITE, Card.LOCOMOTIVE, Card.BLUE, Card.BLUE, Card.ORANGE), trivialCase.faceUpCards());
    }
    @Test
    void deckSize() {
        assertEquals(10, trivialCase.deckSize());
    }
    @Test
    void isEmptyDeck() {
        PublicCardState emptyCase = new PublicCardState(List.of(Card.WHITE, Card.LOCOMOTIVE, Card.BLUE, Card.BLUE, Card.ORANGE),0,1);
        assertEquals(true, emptyCase.isDeckEmpty());
        assertEquals(false, trivialCase.isDeckEmpty());
    }
    @Test
    void discardSize() {
        assertEquals(1, trivialCase.discardsSize());
    }
    @Test
    void faceUpCardOutOf() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            trivialCase.faceUpCard(5);
            trivialCase.faceUpCard(-1);
        });
    }
}
