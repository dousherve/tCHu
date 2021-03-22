package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameStateTest {
    
    private static final Random RNG = TestRandomizer.newRandom();
    private static final SortedBag<Ticket> TICKETS_BAG = SortedBag.of(ChMap.tickets());
    private static final Deck<Ticket> TICKETS_DECK = Deck.of(TICKETS_BAG, RNG);
    private static final PlayerId FIRST_PLAYER = PlayerId.ALL.get(RNG.nextInt(PlayerId.COUNT));
    private static final PlayerId OTHER_PLAYER = FIRST_PLAYER.next();
    
    @Test
    void playerStateWorks() {
        var initial = GameState.initial(TICKETS_BAG, TestRandomizer.newRandom());
    }
    
    @Test
    void topTicketsWorks() {
        var initial = GameState.initial(TICKETS_BAG, TestRandomizer.newRandom());
        
        assertEquals(
                TICKETS_DECK.topCards(TICKETS_BAG.size()),
                initial.topTickets(TICKETS_BAG.size())
        );
    }
    
}