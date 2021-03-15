package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PublicPlayerStateTest {
    
    private static final List<Route> ROUTES = List.of(
            ChMap.routes().get(141 - 92), ChMap.routes().get(108 - 92)
    );
    
    private static final int TICKET_COUNT = 10, CARD_COUNT = 10;
    private static final PublicPlayerState STATE = new PublicPlayerState(TICKET_COUNT, CARD_COUNT, ROUTES);
    
    @Test
    void constructorFails() {
        assertThrows(IllegalArgumentException.class, () -> new PublicPlayerState(-1, 4, ROUTES));
        assertThrows(IllegalArgumentException.class, () -> new PublicPlayerState(3, -141, ROUTES));
    }
    
    @Test
    void constructorWorks() {
        var s1 = new PublicPlayerState(5, 0, ROUTES);
        assertEquals(5, s1.ticketCount());
        assertEquals(0, s1.cardCount());
        
        new PublicPlayerState(0, 5, ROUTES);
        new PublicPlayerState(0, 0, ROUTES);
        new PublicPlayerState(2, 2, Collections.emptyList());
    }

    @Test
    void ticketCount() {
        assertEquals(
                TICKET_COUNT,
                STATE.ticketCount()
        );
    }

    @Test
    void cardCount() {
        assertEquals(
                CARD_COUNT,
                STATE.cardCount()
        );
    }

    @Test
    void routes() {
        assertEquals(ROUTES, STATE.routes());
        
        var state2 = new PublicPlayerState(1, 1, Collections.emptyList());
        assertEquals(Collections.emptyList(), state2.routes());
    }
    
    @Test
    void routesIsImmutable() {
        List<Route> r = new ArrayList<>(ROUTES);
        var state = new PublicPlayerState(TICKET_COUNT, CARD_COUNT, r);
        r.clear();
        assertEquals(ROUTES, state.routes());
        
        var r2 = state.routes();
        assertThrows(UnsupportedOperationException.class, r2::clear);
    }

    @Test
    void carCount() {
        assertEquals(
                Constants.INITIAL_CAR_COUNT - 4 - 4,
                STATE.carCount()
        );
    }

    @Test
    void claimPoints() {
        int claimPoints = 0;
        for (Route r : STATE.routes()) {
            claimPoints += r.claimPoints();
        }
        
        assertEquals(claimPoints, STATE.claimPoints());
    }
    
}