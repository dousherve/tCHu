package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CardStateTest {

    SortedBag<Card> faceUpCards = SortedBag.of(
            List.of(Card.BLUE, Card.RED, Card.LOCOMOTIVE, Card.BLUE, Card.GREEN)
    );
    
    SortedBag<Card> cardsEnough = SortedBag.of(
            List.of(
                    Card.BLUE, Card.RED, Card.LOCOMOTIVE, Card.BLUE,
                    Card.GREEN, Card.GREEN, Card.RED, Card.VIOLET, Card.BLACK
            )
    );
    List<Card> sortedFaceUpCardsEnough = List.of(Card.BLUE, Card.RED, Card.VIOLET, Card.LOCOMOTIVE, Card.GREEN);
    
    SortedBag<Card> cardsNotEnough = SortedBag.of(
            List.of(Card.BLUE, Card.RED, Card.LOCOMOTIVE, Card.BLUE)
    );
    SortedBag<Card> additional = SortedBag.of(
            List.of(Card.BLUE, Card.WHITE, Card.RED)
    );

    CardState cardStateWithDeck = CardState.of(Deck.of(cardsEnough, TestRandomizer.newRandom()));
    CardState cardStateWithoutDeck = CardState.of(Deck.of(faceUpCards, TestRandomizer.newRandom()));

    @Test
    void ofWorks() {
        assertEquals(
                true,
                cardStateWithoutDeck.isDeckEmpty()
        );
        assertEquals(
                List.of(Card.LOCOMOTIVE, Card.RED, Card.BLUE, Card.GREEN, Card.BLUE),
                cardStateWithoutDeck.faceUpCards()
        );
        assertEquals(
                0,
                cardStateWithoutDeck.discardsSize()
        );
        
        assertEquals(
                cardsEnough.size() - Constants.FACE_UP_CARDS_COUNT,
                cardStateWithDeck.deckSize()
        );
        assertEquals(
                false,
                cardStateWithDeck.isDeckEmpty()
        );
        assertEquals(
                sortedFaceUpCardsEnough,
                cardStateWithDeck.faceUpCards()
        );
        assertEquals(
                0,
                cardStateWithDeck.discardsSize()
        );
        assertEquals(
                4,
                cardStateWithDeck.deckSize()
        );
    }

    @Test
    void ofFails() {
        assertThrows(IllegalArgumentException.class, () ->
                CardState.of(Deck.of(cardsNotEnough, TestRandomizer.newRandom()))
        );
    }

    @Test
    void withDrawnFaceUpCardWorks() {
        var expectedReplaced = List.of(Card.BLUE, Card.RED, Card.VIOLET, Card.LOCOMOTIVE, Card.RED);
        var expectedReplaced2 = List.of(Card.BLUE, Card.RED, Card.VIOLET, Card.GREEN, Card.RED);
        
        var newState = cardStateWithDeck.withDrawnFaceUpCard(4);
        assertEquals(
                expectedReplaced,
                newState.faceUpCards()
        );
        assertEquals(
                expectedReplaced2,
                newState.withDrawnFaceUpCard(3).faceUpCards()
        );
        
        cardStateWithDeck.withDrawnFaceUpCard(0);
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
        assertEquals(
                Card.RED,
                cardStateWithDeck.topDeckCard()
        );
    }

    @Test
    void topDeckCardFails() {
        assertThrows(IllegalArgumentException.class, () ->
                cardStateWithoutDeck.topDeckCard()
        );
    }

    @Test
    void withoutTopDeckCardWorks() {
        var newState = cardStateWithDeck.withoutTopDeckCard();
        
        assertEquals(
                cardStateWithDeck.faceUpCards(),
                newState.faceUpCards()
        );
        assertEquals(
                cardStateWithDeck.discardsSize(),
                newState.discardsSize()
        );
        assertEquals(
                cardStateWithDeck.deckSize() - 1,
                newState.deckSize()
        );
        assertEquals(
                Card.GREEN,
                newState.topDeckCard()
        );
    }

    @Test
    void withoutTopDeckCardFails() {
        assertThrows(IllegalArgumentException.class, () ->
                cardStateWithoutDeck.withoutTopDeckCard()
        );
    }
    
    @Test
    void withMoreDiscardedCardsWorks() {
        assertEquals(
                3,
                cardStateWithDeck.withMoreDiscardedCards(additional).discardsSize()
        );
        
        assertEquals(
                cardStateWithDeck.discardsSize(),
                cardStateWithDeck.withMoreDiscardedCards(SortedBag.of()).discardsSize()
        );
    }

    @Test
    void withDeckRecreatedFromDiscardsWorks() {
        var state = cardStateWithoutDeck.withMoreDiscardedCards(cardsEnough);
        
        assertEquals(
                cardStateWithoutDeck.faceUpCards(),
                state.faceUpCards()
        );
        
        assertEquals(
                cardsEnough.size(),
                state.discardsSize()
        );
        
        assertEquals(
                0,
                state.withDeckRecreatedFromDiscards(TestRandomizer.newRandom()).discardsSize()
        );
        
        var shuffledDiscards = List.of(Card.BLUE, Card.RED, Card.VIOLET, Card.LOCOMOTIVE, Card.GREEN, Card.RED, Card.GREEN, Card.BLACK, Card.BLUE);
        
        CardState tempState = state.withDeckRecreatedFromDiscards(TestRandomizer.newRandom());
        for (int i = 0; i < state.discardsSize(); ++i) {
            assertEquals(
                    shuffledDiscards.get(i),
                    tempState.topDeckCard()
            );
            tempState = tempState.withoutTopDeckCard();
        }
    }

    @Test
    void withDeckRecreatedFromDiscardsFails() {
        assertEquals(0, cardStateWithDeck.discardsSize());
        assertThrows(IllegalArgumentException.class, () ->
                cardStateWithDeck.withDeckRecreatedFromDiscards(TestRandomizer.newRandom())
        );
    }
    
}