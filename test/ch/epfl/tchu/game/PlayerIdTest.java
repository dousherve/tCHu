package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerIdTest {
    
    private static final PlayerId P1 = PlayerId.PLAYER_1; 
    private static final PlayerId P2 = PlayerId.PLAYER_2; 

    @Test
    void nextWorks() {
        assertEquals(
                P2, P1.next()
        );
        assertEquals(
                P1, P2.next()
        );
    }

    @Test
    void testALL() {
        assertEquals(
                List.of(P1, P2),
                PlayerId.ALL
        );
    }

    @Test
    void testCOUNT() {
        assertEquals(2, PlayerId.COUNT);
    }
    
}