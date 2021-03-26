package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {
    
    private static final SortedBag<Ticket> TICKETS_BAG = SortedBag.of(ChMap.tickets());
    private static final GameState INITIAL = GameState.initial(TICKETS_BAG, TestRandomizer.newRandom());
    private static final SortedBag<Card> MORE_DISCARD_CARDS = SortedBag.of(List.of(
            Card.YELLOW, Card.BLACK, Card.BLUE, Card.BLACK, Card.LOCOMOTIVE, Card.RED,
            Card.GREEN, Card.GREEN, Card.GREEN, Card.ORANGE, Card.VIOLET, Card.WHITE
    ));
    
    private static final Station BRI = new Station(4, "Brigue");
    private static final Station LOC = new Station(15, "Locarno");
    private static final Station INT = new Station(11, "Interlaken");
    
    @Test
    void initialWorks() {
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; ++i) {
            var initial = GameState.initial(TICKETS_BAG, new Random());
            
            assertTrue(
                    initial.currentPlayerId() == PlayerId.PLAYER_1
                            || initial.currentPlayerId() == PlayerId.PLAYER_2
            );
            assertNull(initial.lastPlayer());
    
            var first = initial.currentPlayerId();
            var firstState = initial.currentPlayerState();
            assertEquals(initial.playerState(first), firstState);
    
            var secondState = initial.playerState(first.next());
            assertTrue(firstState.routes().isEmpty());
            assertTrue(secondState.routes().isEmpty());
    
            assertEquals(4, firstState.cards().size());
            assertEquals(4, secondState.cards().size());
            assertEquals(97, initial.cardState().deckSize());
            assertEquals(0, initial.cardState().discardsSize());
    
            GameState tmpState = initial;
            List<Card> tmpCards = Constants.ALL_CARDS.toList();
            for (int j = 0; j < initial.cardState().deckSize(); ++j) {
                assertTrue(tmpCards.remove(tmpState.topCard()));
                tmpState = tmpState.withoutTopCard();
            }
            assertTrue(tmpState.cardState().isDeckEmpty());
            
            assertEquals(TICKETS_BAG.size(), tmpState.ticketsCount());
            assertTrue(TICKETS_BAG.contains(initial.topTickets(TICKETS_BAG.size())));
            assertTrue(TICKETS_BAG.difference(initial.topTickets(TICKETS_BAG.size())).isEmpty());
            assertTrue(initial.topTickets(TICKETS_BAG.size()).difference(TICKETS_BAG).isEmpty());
        }
    }
    
    @Test
    void playerStateWorks() {
        assertNotNull(INITIAL.playerState(INITIAL.currentPlayerId().next()));
        assertNotNull(INITIAL.currentPlayerState());
    }
    
    @Test
    void topTicketsWorks() {
        assertThrows(IllegalArgumentException.class, () -> INITIAL.topTickets(-1));
        for (int i = 0; i <= INITIAL.ticketsCount(); ++i)
            INITIAL.topTickets(i);
        assertThrows(IllegalArgumentException.class, () -> INITIAL.topTickets(INITIAL.ticketsCount() + 1));
    }
    
    @Test
    void withoutTopTicketsWorks() {
        assertThrows(IllegalArgumentException.class, () -> INITIAL.withoutTopTickets(-1));
        for (int i = 0; i <= INITIAL.ticketsCount(); ++i) {
            var tmp = INITIAL.withoutTopTickets(i);
            assertEquals(INITIAL.ticketsCount() - i, tmp.ticketsCount());
            
            assertEquals(INITIAL.cardState(), tmp.cardState());
            assertEquals(INITIAL.currentPlayerId(), tmp.currentPlayerId());
            assertEquals(INITIAL.lastPlayer(), tmp.lastPlayer());
        }
        assertThrows(IllegalArgumentException.class, () -> INITIAL.withoutTopTickets(INITIAL.ticketsCount() + 1));
    }
    
    @Test
    void topCardAndWithoutTopCardWork() {
        GameState empty = INITIAL;
        for (int i = 0; i < INITIAL.cardState().deckSize(); ++i)  
            empty = empty.withoutTopCard();
    
        assertTrue(empty.cardState().isDeckEmpty());
        
        assertThrows(IllegalArgumentException.class, empty::topCard);
        assertThrows(IllegalArgumentException.class, empty::withoutTopCard);
        
        assertEquals(INITIAL.currentPlayerId(), INITIAL.withoutTopCard().currentPlayerId());
        assertEquals(INITIAL.lastPlayer(), INITIAL.withoutTopCard().lastPlayer());
        assertEquals(
                INITIAL.topTickets(TICKETS_BAG.size()),
                INITIAL.withoutTopCard().topTickets(TICKETS_BAG.size())
        );
    }
    
    @Test
    void withMoreDiscardedCardsWorks() {
        var withDis = INITIAL.withMoreDiscardedCards(MORE_DISCARD_CARDS);
        
        assertEquals(INITIAL.topTickets(TICKETS_BAG.size()), withDis.topTickets(TICKETS_BAG.size()));
        assertEquals(INITIAL.currentPlayerId(), withDis.currentPlayerId());
        assertEquals(INITIAL.lastPlayer(), withDis.lastPlayer());
        assertEquals(
                INITIAL.cardState().discardsSize() + MORE_DISCARD_CARDS.size(),
                withDis.cardState().discardsSize()
        );
    }
    
    @Test
    void withCardsDeckRecreatedIfNeededWorks() {
        var withDis = INITIAL.withMoreDiscardedCards(MORE_DISCARD_CARDS);
        GameState emptyDeck = withDis;
        for (int i = 0; i < withDis.cardState().deckSize(); ++i)
            emptyDeck = emptyDeck.withoutTopCard();
        
        // Not needed
        assertEquals(INITIAL, INITIAL.withCardsDeckRecreatedIfNeeded(TestRandomizer.newRandom()));
        
        // Needed, because empty deck
        final GameState recreatedIfNeeded = emptyDeck.withCardsDeckRecreatedIfNeeded(TestRandomizer.newRandom());
        
        assertEquals(0, recreatedIfNeeded.cardState().discardsSize());
        assertEquals(MORE_DISCARD_CARDS.size(), recreatedIfNeeded.cardState().deckSize());
    
        GameState tmpState = recreatedIfNeeded;
        List<Card> tmpCards = MORE_DISCARD_CARDS.toList();
        for (int i = 0; i < recreatedIfNeeded.cardState().deckSize(); ++i) {
            assertTrue(tmpCards.remove(tmpState.topCard()));
            tmpState = tmpState.withoutTopCard();
        }
        assertTrue(tmpCards.isEmpty());
        assertTrue(tmpState.cardState().isDeckEmpty());
        
        assertEquals(
                emptyDeck.topTickets(TICKETS_BAG.size()),
                recreatedIfNeeded.topTickets(TICKETS_BAG.size())
        );
        assertEquals(
                emptyDeck.currentPlayerId(),
                recreatedIfNeeded.currentPlayerId()
        );
        assertEquals(
                emptyDeck.lastPlayer(),
                recreatedIfNeeded.lastPlayer()
        );
    }
    
    @Test
    void withInitiallyChosenTicketsWorks() {
        for (int i = 1; i < TICKETS_BAG.size(); ++i) {
            var tickets = SortedBag.of(TICKETS_BAG.toList().subList(0, i));
            var state = INITIAL.withInitiallyChosenTickets(PlayerId.PLAYER_1, tickets);
            var state2 = INITIAL.withInitiallyChosenTickets(PlayerId.PLAYER_2, tickets);
            assertThrows(IllegalArgumentException.class, () -> {
                state.withInitiallyChosenTickets(PlayerId.PLAYER_1, tickets);
            });
            assertThrows(IllegalArgumentException.class, () -> {
                state2.withInitiallyChosenTickets(PlayerId.PLAYER_2, tickets);
            });
        }
    
        var tickets = SortedBag.of(TICKETS_BAG.toList().subList(0, 5));
        var tickets2 = SortedBag.of(TICKETS_BAG.toList().subList(0, 12));
        var state = INITIAL.withInitiallyChosenTickets(PlayerId.PLAYER_1, tickets);
        var state2 = INITIAL.withInitiallyChosenTickets(PlayerId.PLAYER_2, tickets2);
        assertEquals(tickets, state.playerState(PlayerId.PLAYER_1).tickets());
        assertEquals(tickets2, state2.playerState(PlayerId.PLAYER_2).tickets());
    }
    
    @Test
    void withChosenAdditionalTicketsWorks() {
        SortedBag<Ticket> drawn = SortedBag.of(TICKETS_BAG.toList().subList(TICKETS_BAG.size() - 5, TICKETS_BAG.size()));
        SortedBag<Ticket> badChosen = SortedBag.of(TICKETS_BAG.toList().subList(TICKETS_BAG.size() - 6, TICKETS_BAG.size() - 3));
        SortedBag<Ticket> chosen = SortedBag.of(TICKETS_BAG.toList().subList(TICKETS_BAG.size() - 3, TICKETS_BAG.size()));
        
        assertThrows(IllegalArgumentException.class, () -> INITIAL.withChosenAdditionalTickets(drawn, badChosen));
        
        var state = INITIAL.withChosenAdditionalTickets(drawn, chosen);
        
        assertEquals(INITIAL.cardState(), state.cardState());
        assertEquals(INITIAL.currentPlayerId(), state.currentPlayerId());
        assertEquals(INITIAL.lastPlayer(), state.lastPlayer());
        
        assertEquals(chosen.union(INITIAL.currentPlayerState().tickets()), state.currentPlayerState().tickets());
        assertEquals(INITIAL.ticketsCount() - drawn.size(), state.ticketsCount());
    
        assertEquals(INITIAL.withoutTopTickets(drawn.size()).topTickets(INITIAL.ticketsCount() - drawn.size()), state.topTickets(state.ticketsCount()));
    }
    
    @Test
    void withDrawnFaceUpCardWorks() {
        GameState emptyStateDeck = INITIAL;
        for (int i = 0; i < INITIAL.cardState().deckSize(); ++i)
            emptyStateDeck = emptyStateDeck.withoutTopCard();
        
        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            var state = INITIAL.withDrawnFaceUpCard(slot);
            assertEquals(
                    INITIAL.currentPlayerState().cardCount() + 1,
                    state.currentPlayerState().cardCount()
            );
            assertEquals(INITIAL.topCard(), state.cardState().faceUpCard(slot));
    
            GameState finalEmptyStateDeck = emptyStateDeck;
            assertThrows(IllegalArgumentException.class, () -> finalEmptyStateDeck.withDrawnFaceUpCard(slot));
        }
    }
    
    @Test
    void withBlindlyDrawnCardWorks() {
        GameState emptyStateDeck = INITIAL;
        for (int i = 0; i < INITIAL.cardState().deckSize(); ++i)
            emptyStateDeck = emptyStateDeck.withoutTopCard();
    
        assertThrows(IllegalArgumentException.class, emptyStateDeck::withBlindlyDrawnCard);
        
        var state = INITIAL.withBlindlyDrawnCard();
        assertEquals(
                INITIAL.currentPlayerState().cards().union(SortedBag.of(INITIAL.topCard())),
                state.currentPlayerState().cards()
        );
    
        GameState tmpInit = INITIAL;
        GameState tmpState = state;
        for (int i = 0; i < state.cardState().deckSize(); ++i) {
            assertEquals(tmpInit.withoutTopCard().topCard(), tmpState.topCard());
            tmpInit = tmpInit.withoutTopCard();
            tmpState = tmpState.withoutTopCard();
        }
    }
    
    @Test
    void withClaimedRouteWorks() {
        var state = INITIAL;
        final SortedBag<Card> cards = SortedBag.of(1, Card.RED, 2, Card.YELLOW);
        for (Route r : ChMap.routes()) {
            var newRoutes = new ArrayList<>(state.currentPlayerState().routes());
            newRoutes.add(r);
            state = state.withClaimedRoute(r, cards);
            
            assertEquals(newRoutes, state.currentPlayerState().routes());
            assertEquals(cards.size() * state.currentPlayerState().routes().size(), state.cardState().discardsSize());
        }
    }
    
    @Test
    void lastTurnBeginsWorks() {
        var longRoute = new Route("BRI_LOC_1", BRI, LOC, 6, Route.Level.UNDERGROUND, null);
        var shortRoute = new Route("BRI_INT_1", BRI, INT, 2, Route.Level.UNDERGROUND, Color.WHITE);
    
        var state = INITIAL;
        for (int i = 0; i < 6; ++i)
            state = state.withClaimedRoute(longRoute, SortedBag.of(1, Card.RED, 2, Card.YELLOW));
        state = state.withClaimedRoute(shortRoute, SortedBag.of(2, Card.RED));
        
        assertNull(state.lastPlayer());
        assertTrue(state.lastTurnBegins());
        
        assertNull(INITIAL.lastPlayer());
        assertFalse(INITIAL.lastTurnBegins());
    }
    
    @Test
    void forNextTurnWorks() {
        var longRoute = new Route("BRI_LOC_1", BRI, LOC, 6, Route.Level.UNDERGROUND, null);
        var shortRoute = new Route("BRI_INT_1", BRI, INT, 2, Route.Level.UNDERGROUND, Color.WHITE);
    
        var state = INITIAL;
        var nextState = INITIAL.forNextTurn();
        for (int i = 0; i < 6; ++i)
            state = state.withClaimedRoute(longRoute, SortedBag.of(1, Card.RED, 2, Card.YELLOW));
        state = state.withClaimedRoute(shortRoute, SortedBag.of(2, Card.RED));
        
        assertEquals(INITIAL.currentPlayerId().next(), nextState.currentPlayerId());
        assertFalse(nextState.lastTurnBegins());
        assertNull(nextState.forNextTurn().lastPlayer());
        
        assertTrue(state.lastTurnBegins());
        assertEquals(state.currentPlayerId(), state.forNextTurn().lastPlayer());
        assertEquals(state.currentPlayerId().next(), state.forNextTurn().currentPlayerId());
    }
    
}