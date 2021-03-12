package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeckTest {

    List<Card> cardsList = List.of(Card.BLUE, Card.RED, Card.LOCOMOTIVE, Card.ORANGE, Card.BLUE, Card.BLACK);
    List<Card> shuffledCards = List.of(Card.BLUE, Card.RED, Card.ORANGE, Card.LOCOMOTIVE, Card.BLACK, Card.BLUE);
    
    SortedBag<Card> cards = SortedBag.of(cardsList);

    SortedBag<Card> emptyBag = SortedBag.of();

    Deck<Card> deck = Deck.of(cards, TestRandomizer.newRandom());

    Deck<Card> emptyDeck = Deck.of(emptyBag, TestRandomizer.newRandom());

    @Test
    void ofWorks() {
        for (int i = cards.size() - 1; i >= 0; --i) {
            assertEquals(
                    shuffledCards.get(i),
                    deck.withoutTopCards(cards.size() - 1 - i).topCard()
            );
        }
    }

    @Test
    void sizeWorks() {
        assertEquals(cardsList.size(), deck.size());
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
        assertEquals(
                Card.BLUE,
                deck.topCard()
        );
    }

    @Test
    void topCardFailsOnEmptyDeck() {
        assertThrows(IllegalArgumentException.class, () ->
            emptyDeck.topCard()
        );
    }

    @Test
    void withoutTopCardWorks() {
        var deckWithoutTopCard = deck.withoutTopCard();
        
        assertEquals(deck.size() - 1, deckWithoutTopCard.size());
        assertEquals(Card.BLACK, deckWithoutTopCard.topCard());
        assertEquals(Card.LOCOMOTIVE, deckWithoutTopCard.withoutTopCard().topCard());
    }

    @Test
    void withoutTopCardFailsOnEmptyDeck() {
        assertThrows(IllegalArgumentException.class, () ->
                emptyDeck.withoutTopCard()
        );
    }

    @Test
    void topCardsWorks() {
        assertEquals(
                SortedBag.of(List.of(Card.BLUE, Card.LOCOMOTIVE, Card.BLACK)),
                deck.topCards(3)
        );
        assertEquals(
                emptyBag,
                deck.topCards(0)
        );
        assertEquals(
                cards,
                deck.topCards(deck.size())
        );
    
        deck.topCards(0);
        deck.topCards(deck.size());
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
        var withoutTwoTopCards = deck.withoutTopCards(2);
        
        assertEquals(deck.size() - 2, withoutTwoTopCards.size());
        
        assertEquals(
                Card.LOCOMOTIVE,
                withoutTwoTopCards.topCard()
        );
        assertEquals(
                Card.ORANGE,
                withoutTwoTopCards.withoutTopCard().topCard()
        );
        assertEquals(
                Card.RED,
                withoutTwoTopCards.withoutTopCard().withoutTopCard().topCard()
        );
        assertEquals(
                Card.BLUE,
                withoutTwoTopCards.withoutTopCard().withoutTopCard().withoutTopCard().topCard()
        );
    
        deck.topCards(0);
        deck.topCards(deck.size());
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