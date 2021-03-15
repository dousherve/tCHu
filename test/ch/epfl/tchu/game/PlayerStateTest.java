package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlayerStateTest {
    
    @Test
    void initialFails() {
        assertThrows(IllegalArgumentException.class, () -> PlayerState.initial(SortedBag.of()));
        assertThrows(IllegalArgumentException.class, () -> PlayerState.initial(SortedBag.of(
                List.of(Card.GREEN, Card.LOCOMOTIVE, Card.BLUE)
        )));
    }
    
    @Test
    void initial() {
        var bag = SortedBag.of(Constants.INITIAL_CARDS_COUNT, Card.RED);
        var state = PlayerState.initial(bag);
        
        assertEquals(
                SortedBag.of(),
                state.tickets()
        );
    
        assertEquals(
                bag,
                state.cards()
        );
        
        assertEquals(
                Collections.emptyList(),
                state.routes()
        );
    }

    @Test
    void tickets() {
    }

    @Test
    void withAddedTickets() {
    }

    @Test
    void cards() {
    }

    @Test
    void withAddedCard() {
    }

    @Test
    void withAddedCards() {
    }

    @Test
    void canClaimRoute() {
    }

    @Test
    void possibleClaimCards() {
    }

    @Test
    void possibleAdditionalCards() {
    }

    @Test
    void withClaimedRoute() {
    }

    @Test
    void ticketPoints() {
    }

    @Test
    void finalPoints() {
    }
    
}