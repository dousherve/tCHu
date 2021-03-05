package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RouteTest {
    
    private static Route getRoute(int lineNumber) {
        return ChMap.routes().get(lineNumber - 92);
    }

    @Test
    void constructorFailsOnEqualsStations() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Route("LAUSANNE", ChMap.stations().get(0), ChMap.stations().get(0),
                    2, Route.Level.OVERGROUND, null
            );
        });
    }

    @Test
    void constructorFailsOnInvalidLength() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Route("LAUSANNE", ChMap.stations().get(0), ChMap.stations().get(1),
                    Constants.MIN_ROUTE_LENGTH - 1, Route.Level.OVERGROUND, null
            );
        });
        new Route("LAUSANNE", ChMap.stations().get(0), ChMap.stations().get(1), 
                1, Route.Level.OVERGROUND, null
        );
        assertThrows(IllegalArgumentException.class, () -> {
            new Route("LAUSANNE", ChMap.stations().get(0), ChMap.stations().get(1),
                    Constants.MAX_ROUTE_LENGTH + 1, Route.Level.OVERGROUND, null
            );
        });
    }

    @Test
    void constructorFailsOnNullArguments() {
        assertThrows(NullPointerException.class, () -> {
            new Route(null, ChMap.stations().get(0), ChMap.stations().get(1), 1,
                    Route.Level.OVERGROUND, null);
        });
        assertThrows(NullPointerException.class, () -> {
            new Route("LAUSANNE", null, ChMap.stations().get(1), 1,
                    Route.Level.OVERGROUND, null);
        });
        assertThrows(NullPointerException.class, () -> {
            new Route("LAUSANNE", ChMap.stations().get(0), null, 1,
                    Route.Level.OVERGROUND, null);
        });
        assertThrows(NullPointerException.class, () -> {
            new Route("LAUSANNE", ChMap.stations().get(0), ChMap.stations().get(1),
                    1, null, null);
        });
    }

    @Test
    void id() {
        Route route = new Route("LAUSANNE", ChMap.stations().get(0), ChMap.stations().get(1),
                1, Route.Level.OVERGROUND, Color.RED);
        assertEquals("LAUSANNE", route.id());
    }

    @Test
    void station1() {
        Route route = new Route("LAUSANNE", ChMap.stations().get(0), ChMap.stations().get(1),
                1, Route.Level.OVERGROUND, Color.RED);
        assertEquals(ChMap.stations().get(0), route.station1());
    }

    @Test
    void station2() {
        Route route = new Route("LAUSANNE", ChMap.stations().get(0), ChMap.stations().get(1),
                1, Route.Level.OVERGROUND, Color.RED);
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
    void stationOppositeWorks() {
        Route route = new Route("LAUSANNE", ChMap.stations().get(0), ChMap.stations().get(1), 1, Route.Level.OVERGROUND, Color.RED);
        assertEquals(ChMap.stations().get(1), route.stationOpposite(ChMap.stations().get(0)));
    }
    
    @Test
    void stationOppositeFailsOnBadArgument() {
        Route route = new Route("LAUSANNE", ChMap.stations().get(0), ChMap.stations().get(1), 1, Route.Level.OVERGROUND, Color.RED);
        assertThrows(IllegalArgumentException.class, () -> route.stationOpposite(ChMap.stations().get(5)));
    }

    @Test
    void possibleClaimCardsWorksOnColoredRoute() {
        final Route r = getRoute(110);
        assertEquals(1, r.possibleClaimCards().size());
        assertEquals(
                "[{2×RED}]",
                r.possibleClaimCards().toString()
        );
    
        final Route r2 = getRoute(138);
        assertEquals(1, r2.possibleClaimCards().size());
        assertEquals(
                "[{4×BLUE}]",
                r2.possibleClaimCards().toString()
        );
    }

    @Test
    void possibleClaimCardsWorksOnNeutralRoute() {
        final Route r = getRoute(140);
        assertEquals(8, r.possibleClaimCards().size());
        assertEquals(
                "[{6×BLACK}, {6×VIOLET}, {6×BLUE}, {6×GREEN}, {6×YELLOW}, {6×ORANGE}, {6×RED}, {6×WHITE}]",
                r.possibleClaimCards().toString()
        );
    }

    @Test
    void possibleClaimCardsWorksOnColoredTunnel() {
        final Route r = ChMap.routes().get(1);
    
        assertEquals(2, r.possibleClaimCards().size());
        assertEquals(
                "[{RED}, {LOCOMOTIVE}]",
                r.possibleClaimCards().toString()
        );
        
        final Route r2 = getRoute(131);
        assertEquals(4, r2.possibleClaimCards().size());
        assertEquals(
                "[{3×WHITE}, {2×WHITE, LOCOMOTIVE}, {WHITE, 2×LOCOMOTIVE}, {3×LOCOMOTIVE}]",
                r2.possibleClaimCards().toString()
        );
    }

    @Test
    void possibleClaimCardsWorksOnNeutralTunnel() {
        final Route r = getRoute(133);
        final var POSSIBLE_CLAIM_CARDS = r.possibleClaimCards();
        assertEquals(17, POSSIBLE_CLAIM_CARDS.size());
        assertEquals(
                "[{2×BLACK}, {2×VIOLET}, {2×BLUE}, {2×GREEN}, {2×YELLOW}, {2×ORANGE}, {2×RED}, {2×WHITE}, {BLACK, LOCOMOTIVE}, {VIOLET, LOCOMOTIVE}, {BLUE, LOCOMOTIVE}, {GREEN, LOCOMOTIVE}, {YELLOW, LOCOMOTIVE}, {ORANGE, LOCOMOTIVE}, {RED, LOCOMOTIVE}, {WHITE, LOCOMOTIVE}, {2×LOCOMOTIVE}]",
                POSSIBLE_CLAIM_CARDS.toString()
        );
    
        final Route r2 = getRoute(103);
        final var POSSIBLE_CLAIM_CARDS2 = r2.possibleClaimCards();
    
        assertEquals(33, r2.possibleClaimCards().size());
        assertEquals(
                "[{4×BLACK}, {4×VIOLET}, {4×BLUE}, {4×GREEN}, {4×YELLOW}, {4×ORANGE}, {4×RED}, {4×WHITE}, {3×BLACK, LOCOMOTIVE}, {3×VIOLET, LOCOMOTIVE}, {3×BLUE, LOCOMOTIVE}, {3×GREEN, LOCOMOTIVE}, {3×YELLOW, LOCOMOTIVE}, {3×ORANGE, LOCOMOTIVE}, {3×RED, LOCOMOTIVE}, {3×WHITE, LOCOMOTIVE}, {2×BLACK, 2×LOCOMOTIVE}, {2×VIOLET, 2×LOCOMOTIVE}, {2×BLUE, 2×LOCOMOTIVE}, {2×GREEN, 2×LOCOMOTIVE}, {2×YELLOW, 2×LOCOMOTIVE}, {2×ORANGE, 2×LOCOMOTIVE}, {2×RED, 2×LOCOMOTIVE}, {2×WHITE, 2×LOCOMOTIVE}, {BLACK, 3×LOCOMOTIVE}, {VIOLET, 3×LOCOMOTIVE}, {BLUE, 3×LOCOMOTIVE}, {GREEN, 3×LOCOMOTIVE}, {YELLOW, 3×LOCOMOTIVE}, {ORANGE, 3×LOCOMOTIVE}, {RED, 3×LOCOMOTIVE}, {WHITE, 3×LOCOMOTIVE}, {4×LOCOMOTIVE}]",
                POSSIBLE_CLAIM_CARDS2.toString()
        );
    }

    @Test
    void additionalClaimCardsWorksWithLocomotiveCards() {
        final Route r = getRoute(150);
        
        final SortedBag<Card> CLAIM = SortedBag.of(1, Card.LOCOMOTIVE, 3, Card.YELLOW);
        final SortedBag<Card> CLAIM2 = SortedBag.of(2, Card.LOCOMOTIVE, 1, Card.YELLOW);
        
        final SortedBag<Card> DRAWN = SortedBag.of(1, Card.YELLOW, 2, Card.LOCOMOTIVE);
        final SortedBag<Card> DRAWN2 = SortedBag.of(2, Card.BLACK, 1, Card.LOCOMOTIVE);
        final SortedBag<Card> DRAWN3 = SortedBag.of(3, Card.LOCOMOTIVE);
        
        assertEquals(3, r.additionalClaimCardsCount(CLAIM, DRAWN));
        assertEquals(1, r.additionalClaimCardsCount(CLAIM, DRAWN2));
        assertEquals(3, r.additionalClaimCardsCount(CLAIM, DRAWN3));
        assertEquals(1, r.additionalClaimCardsCount(CLAIM2, DRAWN2));
    }
    
    @Test
    void additionalClaimCardsCountFailsOnOvergroundRoute() {
        assertThrows(IllegalArgumentException.class, () -> getRoute(140)
                .additionalClaimCardsCount(SortedBag.of(3, Card.BLACK), SortedBag.of(3, Card.BLACK)));
    }

    @Test
    void additionalClaimCardsCountFailsIfNotThreeDrawnCards() {
        assertThrows(IllegalArgumentException.class, () -> getRoute(142)
                .additionalClaimCardsCount(SortedBag.of(3, Card.BLACK), SortedBag.of(2, Card.YELLOW)));
    }

    @Test
    void additionalClaimCardsCountWorksWithoutLocomotiveDrawnCard() {
        final Route r = getRoute(150);
    
        final SortedBag<Card> CLAIM = SortedBag.of(1, Card.LOCOMOTIVE, 3, Card.YELLOW);
        final SortedBag<Card> CLAIM2 = SortedBag.of(4, Card.LOCOMOTIVE);
        final SortedBag<Card> CLAIM3 = SortedBag.of(4, Card.YELLOW);
        final SortedBag<Card> CLAIM4 = SortedBag.of(4, Card.RED);
    
        final SortedBag<Card> DRAWN = SortedBag.of(1, Card.YELLOW, 2, Card.BLUE);
        final SortedBag<Card> DRAWN2 = SortedBag.of(2, Card.YELLOW, 1, Card.WHITE);
        final SortedBag<Card> DRAWN3 = SortedBag.of(3, Card.YELLOW);
    
        assertEquals(1, r.additionalClaimCardsCount(CLAIM, DRAWN));
        assertEquals(2, r.additionalClaimCardsCount(CLAIM, DRAWN2));
        assertEquals(3, r.additionalClaimCardsCount(CLAIM, DRAWN3));
    
        assertEquals(0, r.additionalClaimCardsCount(CLAIM2, DRAWN));
        assertEquals(0, r.additionalClaimCardsCount(CLAIM2, DRAWN2));
        assertEquals(0, r.additionalClaimCardsCount(CLAIM2, DRAWN3));
    
        assertEquals(1, r.additionalClaimCardsCount(CLAIM3, DRAWN));
        assertEquals(2, r.additionalClaimCardsCount(CLAIM3, DRAWN2));
        assertEquals(3, r.additionalClaimCardsCount(CLAIM3, DRAWN3));
    
        assertEquals(0, r.additionalClaimCardsCount(CLAIM4, DRAWN));
        assertEquals(0, r.additionalClaimCardsCount(CLAIM4, DRAWN2));
        assertEquals(0, r.additionalClaimCardsCount(CLAIM4, DRAWN3));
    }
    
    @Test
    void claimPointsWorks() {
        assertEquals(1, getRoute(171).claimPoints());
        assertEquals(2, getRoute(110).claimPoints());
        assertEquals(4, getRoute(107).claimPoints());
        assertEquals(7, getRoute(108).claimPoints());
        assertEquals(10, getRoute(117).claimPoints());
        assertEquals(15, getRoute(140).claimPoints());
    }
    
}