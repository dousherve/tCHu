package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrailTest {
    
    private static final int CONSTANTS_START_LINE = 92;
    
    private static final Trail EMPTY_TRAIL = Trail.longest(Collections.emptyList());
    
    private static Route getRoute(int lineNumber) {
        return ChMap.routes().get(lineNumber - CONSTANTS_START_LINE);
    }
    
    @Test
    void longestWorksOnEmptyList() {
        var trail = Trail.longest(Collections.emptyList());
        assertEquals("Chemin vide", trail.toString());
    }

    @Test
    void longestWorks() {
        List<Route> routes = new ArrayList<>();
        routes.add(getRoute(108));
        routes.add(getRoute(158));
        routes.add(getRoute(157));
        routes.add(getRoute(110));
        routes.add(getRoute(105));
        routes.add(getRoute(111));
        
        var trail = Trail.longest(routes);
        
        assertEquals(
                "Lucerne - Berne - Neuchâtel - Soleure - Berne - Fribourg (13)",
                trail.toString()
        );
        
        List<Route> routes2 = new ArrayList<>();
        routes2.add(getRoute(136));
        routes2.add(getRoute(141));
        routes2.add(getRoute(105));
        routes2.add(getRoute(152));
        
        var trail2 = Trail.longest(routes2);
        
        assertEquals(
                "Interlaken - Lucerne - Olten (7)",
                trail2.toString()
        );
    }

    @Test
    void lengthWorks() {
        List<Route> routes = new ArrayList<>();
        routes.add(getRoute(175));
        routes.add(getRoute(163));
        routes.add(getRoute(161));
        routes.add(getRoute(125));
        
        var trail = Trail.longest(routes);
        
        assertTrue(trail.toString().contains("13"));
    }

    @Test
    void station1Works() {
        final Route R = getRoute(125);
        List<Route> routes = new ArrayList<>();
        routes.add(R);
        
        var trail = Trail.longest(routes);
        assertEquals(R.station1(), trail.station1());
    }

    @Test
    void station1ReturnNullOnLengthZero() {
        assertNull(EMPTY_TRAIL.station1());
    }

    @Test
    void station2Works() {
        final Route R = getRoute(125);
        List<Route> routes = new ArrayList<>();
        routes.add(R);
    
        var trail = Trail.longest(routes);
        assertEquals(R.station2(), trail.station2());
    }

    @Test
    void station2ReturnNullOnLengthZero() {
        assertNull(EMPTY_TRAIL.station2());
    }
    
}