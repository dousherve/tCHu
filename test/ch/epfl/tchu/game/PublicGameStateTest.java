package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PublicGameStateTest {
    
    private static final PublicCardState CARD_STATE1 = new PublicCardState(
            List.of(Card.BLACK, Card.BLUE, Card.BLUE, Card.ORANGE, Card.LOCOMOTIVE),
            97, 0
    );
    private static final PublicCardState CARD_STATE_NOT_ENOUGH = new PublicCardState(
            List.of(Card.BLACK, Card.BLUE, Card.BLUE, Card.ORANGE, Card.LOCOMOTIVE),
            3, 1
    );
    private static final PublicCardState CARD_STATE_JUST_ENOUGH = new PublicCardState(
            List.of(Card.BLACK, Card.BLUE, Card.BLUE, Card.ORANGE, Card.LOCOMOTIVE),
            3, 2
    );
    
    private static final List<Route> ROUTES1 = List.of(ChMap.routes().get(16), ChMap.routes().get(49));
    private static final List<Route> ROUTES2 = List.of(ChMap.routes().get(17));
    
    private static final PublicPlayerState EMPTY_PLAYER_STATE = new PublicPlayerState(0, 0, List.of());
    private static final PublicPlayerState PLAYER_STATE1 = new PublicPlayerState(10, 5, ROUTES1);
    private static final PublicPlayerState PLAYER_STATE2 = new PublicPlayerState(5, 2, ROUTES2);
    
    private static final Map<PlayerId, PublicPlayerState> EMPTY_MAP = Map.of();
    private static final Map<PlayerId, PublicPlayerState> MAP_EMPTY_STATES = Map.of(
            PlayerId.PLAYER_1, EMPTY_PLAYER_STATE,
            PlayerId.PLAYER_2, EMPTY_PLAYER_STATE
    );
    private static final Map<PlayerId, PublicPlayerState> MAP1 = Map.of(
            PlayerId.PLAYER_1, PLAYER_STATE1,
            PlayerId.PLAYER_2, PLAYER_STATE2
    );
    private static final Map<PlayerId, PublicPlayerState> MAP_TOO_SHORT = Map.of(
            PlayerId.PLAYER_1, PLAYER_STATE1
    );
    
    @Test
    void constructorFailsOnBadArguments() {
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicGameState(-5, CARD_STATE1, PlayerId.PLAYER_1, MAP1, PlayerId.PLAYER_2);
        });
    
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicGameState(10, CARD_STATE1, PlayerId.PLAYER_1, MAP_TOO_SHORT, PlayerId.PLAYER_2);
        });
    
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicGameState(10, CARD_STATE1, PlayerId.PLAYER_1, EMPTY_MAP, PlayerId.PLAYER_2);
        });
        
        assertThrows(NullPointerException.class, () -> {
            new PublicGameState(ChMap.tickets().size(), null, PlayerId.PLAYER_1, MAP1, PlayerId.PLAYER_2);
        });
        
        assertThrows(NullPointerException.class, () -> {
            new PublicGameState(ChMap.tickets().size(), CARD_STATE1, null, MAP1, PlayerId.PLAYER_2);
        });
    
        assertThrows(NullPointerException.class, () -> {
            new PublicGameState(ChMap.tickets().size(), CARD_STATE1, PlayerId.PLAYER_2, null, PlayerId.PLAYER_1);
        });
    }
    
    @Test
    void constructorWorksOnValidArguments() {
        new PublicGameState(10, CARD_STATE1, PlayerId.PLAYER_1, MAP1, PlayerId.PLAYER_2);
        new PublicGameState(0, CARD_STATE1, PlayerId.PLAYER_1, MAP1, PlayerId.PLAYER_2);
        new PublicGameState(10, CARD_STATE1, PlayerId.PLAYER_2, MAP_EMPTY_STATES, null);
    }
    
    @Test
    void ticketsCount() {
        var state1 = new PublicGameState(10, CARD_STATE1, PlayerId.PLAYER_1, MAP1, PlayerId.PLAYER_2);
        assertEquals(10, state1.ticketsCount());
        
        var state2 = new PublicGameState(0, CARD_STATE1, PlayerId.PLAYER_1, MAP1, PlayerId.PLAYER_2);
        assertEquals(0, state2.ticketsCount());
    
        var state3 = new PublicGameState(ChMap.tickets().size(), CARD_STATE1, PlayerId.PLAYER_1, MAP1, PlayerId.PLAYER_2);
        assertEquals(ChMap.tickets().size(), state3.ticketsCount());
    }
    
    @Test
    void canDrawTickets() {
        var state1 = new PublicGameState(10, CARD_STATE1, PlayerId.PLAYER_1, MAP1, PlayerId.PLAYER_2);
        assertTrue(state1.canDrawTickets());
        
        var state2 = new PublicGameState(0, CARD_STATE1, PlayerId.PLAYER_1, MAP1, PlayerId.PLAYER_2);
        assertFalse(state2.canDrawTickets());
    }
    
    @Test
    void cardState() {
        var state1 = new PublicGameState(10, CARD_STATE1, PlayerId.PLAYER_1, MAP1, PlayerId.PLAYER_2);
        assertEquals(CARD_STATE1, state1.cardState());
    
        var state2 = new PublicGameState(10, CARD_STATE_NOT_ENOUGH, PlayerId.PLAYER_1, MAP1, PlayerId.PLAYER_2);
        assertEquals(CARD_STATE_NOT_ENOUGH, state2.cardState());
    }
    
    @Test
    void canDrawCards() {
        var state1 = new PublicGameState(10, CARD_STATE1, PlayerId.PLAYER_1, MAP1, PlayerId.PLAYER_2);
        assertTrue(state1.canDrawCards());
    
        var state2 = new PublicGameState(10, CARD_STATE_NOT_ENOUGH, PlayerId.PLAYER_1, MAP1, PlayerId.PLAYER_2);
        assertFalse(state2.canDrawCards());
    
        var state3 = new PublicGameState(10, CARD_STATE_JUST_ENOUGH, PlayerId.PLAYER_1, MAP1, PlayerId.PLAYER_2);
        assertTrue(state3.canDrawCards());
    }
    
    @Test
    void currentPlayerId() {
        var state1 = new PublicGameState(10, CARD_STATE1, PlayerId.PLAYER_1, MAP1, PlayerId.PLAYER_2);
        assertEquals(PlayerId.PLAYER_1, state1.currentPlayerId());
        assertNotEquals(PlayerId.PLAYER_2, state1.currentPlayerId());
    
        var state2 = new PublicGameState(10, CARD_STATE1, PlayerId.PLAYER_2, MAP1, PlayerId.PLAYER_2);
        assertEquals(PlayerId.PLAYER_2, state2.currentPlayerId());
    }
    
    @Test
    void playerState() {
        var state1 = new PublicGameState(10, CARD_STATE1, PlayerId.PLAYER_1, MAP1, PlayerId.PLAYER_2);
        assertEquals(PLAYER_STATE1, state1.playerState(PlayerId.PLAYER_1));
        assertEquals(PLAYER_STATE2, state1.playerState(PlayerId.PLAYER_2));
    
        var state2 = new PublicGameState(10, CARD_STATE1, PlayerId.PLAYER_1, MAP_EMPTY_STATES, PlayerId.PLAYER_2);
        assertEquals(EMPTY_PLAYER_STATE, state2.playerState(PlayerId.PLAYER_1));
        assertEquals(EMPTY_PLAYER_STATE, state2.playerState(PlayerId.PLAYER_2));
    }
    
    @Test
    void currentPlayerState() {
        var state1 = new PublicGameState(10, CARD_STATE1, PlayerId.PLAYER_1, MAP1, PlayerId.PLAYER_2);
        assertEquals(PLAYER_STATE1, state1.currentPlayerState());
        
        var state2 = new PublicGameState(10, CARD_STATE1, PlayerId.PLAYER_2, MAP_EMPTY_STATES, PlayerId.PLAYER_2);
        assertEquals(EMPTY_PLAYER_STATE, state2.currentPlayerState());
    }
    
    @Test
    void claimedRoutes() {
        var state1 = new PublicGameState(10, CARD_STATE1, PlayerId.PLAYER_1, MAP1, PlayerId.PLAYER_2);
        
        var expectedClaimedRoutes = new ArrayList<>(state1.playerState(PlayerId.PLAYER_1).routes());
        expectedClaimedRoutes.addAll(state1.playerState(PlayerId.PLAYER_2).routes());
        
        assertTrue(
                state1.claimedRoutes().containsAll(expectedClaimedRoutes) 
                        && expectedClaimedRoutes.containsAll(state1.claimedRoutes())
        );
        assertThrows(UnsupportedOperationException.class, state1.claimedRoutes()::clear);
    }
    
    @Test
    void lastPlayer() {
        var state1 = new PublicGameState(10, CARD_STATE1, PlayerId.PLAYER_1, MAP1, PlayerId.PLAYER_2);
        assertEquals(PlayerId.PLAYER_2, state1.lastPlayer());
    
        var state2 = new PublicGameState(10, CARD_STATE1, PlayerId.PLAYER_1, MAP1, PlayerId.PLAYER_1);
        assertEquals(PlayerId.PLAYER_1, state2.lastPlayer());
    
        var state3 = new PublicGameState(10, CARD_STATE1, PlayerId.PLAYER_1, MAP1, null);
        assertNull(state3.lastPlayer());
    }
    
}