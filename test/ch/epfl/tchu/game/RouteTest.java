package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RouteTest {

    @Test
    void constructorFailsOnEqualsStations() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Route("LAUSANNE", ChMap.stations().get(0), ChMap.stations().get(0), 2, Route.Level.OVERGROUND, null);
        });
    }

    @Test
    void constructorFailsOnInvalidLength() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Route("LAUSANNE", ChMap.stations().get(0), ChMap.stations().get(1), Constants.MIN_ROUTE_LENGTH - 1, Route.Level.OVERGROUND, null);
        });
        new Route("LAUSANNE", ChMap.stations().get(0), ChMap.stations().get(1), 1, Route.Level.OVERGROUND, null);
        assertThrows(IllegalArgumentException.class, () -> {
            new Route("LAUSANNE", ChMap.stations().get(0), ChMap.stations().get(1), Constants.MAX_ROUTE_LENGTH + 1, Route.Level.OVERGROUND, null);
        });
    }

    @Test
    void constructorFailsOnNullArguments() {
        assertThrows(NullPointerException.class, () -> {
            new Route(null, ChMap.stations().get(0), ChMap.stations().get(1), 1, Route.Level.OVERGROUND, null);
        });
        assertThrows(NullPointerException.class, () -> {
            new Route("LAUSANNE", null, ChMap.stations().get(1), 1, Route.Level.OVERGROUND, null);
        });
        assertThrows(NullPointerException.class, () -> {
            new Route("LAUSANNE", ChMap.stations().get(0), null, 1, Route.Level.OVERGROUND, null);
        });
        assertThrows(NullPointerException.class, () -> {
            new Route("LAUSANNE", ChMap.stations().get(0), ChMap.stations().get(1), 1, null, null);
        });
    }

    @Test
    void id() {
        Route route = new Route("LAUSANNE", ChMap.stations().get(0), ChMap.stations().get(1), 1, Route.Level.OVERGROUND, Color.RED);
        assertEquals("LAUSANNE", route.id());
    }

    @Test
    void station1() {
        Route route = new Route("LAUSANNE", ChMap.stations().get(0), ChMap.stations().get(1), 1, Route.Level.OVERGROUND, Color.RED);
        assertEquals(ChMap.stations().get(0), route.station1());
    }

    @Test
    void station2() {
        Route route = new Route("LAUSANNE", ChMap.stations().get(0), ChMap.stations().get(1), 1, Route.Level.OVERGROUND, Color.RED);
        assertEquals(ChMap.stations().get(1), route.station2());
    }

    @Test
    void length() {
        Route route = new Route("LAUSANNE", ChMap.stations().get(0), ChMap.stations().get(1), 1, Route.Level.OVERGROUND, Color.RED);
        assertEquals(1, route.length());
    }

    @Test
    void level() {
        Route route = new Route("LAUSANNE", ChMap.stations().get(0), ChMap.stations().get(1), 1, Route.Level.OVERGROUND, Color.RED);
        assertEquals(Route.Level.OVERGROUND, route.level());
    }

    @Test
    void color() {
        Route route = new Route("LAUSANNE", ChMap.stations().get(0), ChMap.stations().get(1), 1, Route.Level.OVERGROUND, Color.RED);
        assertEquals(Color.RED, route.color());
    }

    @Test
    void stations() {
        Route route = new Route("LAUSANNE", ChMap.stations().get(0), ChMap.stations().get(1), 1, Route.Level.OVERGROUND, Color.RED);
        assertEquals(List.of(ChMap.stations().get(0), ChMap.stations().get(1)), route.stations());
    }

    @Test
    void stationOpposite() {

    }

    @Test
    void possibleClaimCardsWorks() {
        final int INDEX = 132 - 91;
        final int INDEX2 = 102 - 91;
        final Route r = ChMap.routes().get(INDEX);
        final Route r2 = ChMap.routes().get(INDEX2);

        System.out.println(r.possibleClaimCards());
        assertEquals(17, r.possibleClaimCards().size());

        System.out.println(r2.possibleClaimCards());
        assertEquals(33, r2.possibleClaimCards().size());
    }

    @Test
    void additionalClaimCardsCount() {
    }

    @Test
    void claimPoints() {
    }
}