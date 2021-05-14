package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerStateTest {
    
    private static final SortedBag<Ticket> TICKETS = SortedBag.of(ChMap.tickets().subList(0, 3));
    private static final SortedBag<Card> CARDS = SortedBag.of(5, Card.BLACK, 1, Card.LOCOMOTIVE);
    private static final List<Route> ROUTES = ChMap.routes().subList(0, 10);
    
    private static final SortedBag<Ticket> ADDED_TICKETS = SortedBag.of(ChMap.tickets().subList(4, 6));
    
    private static final PlayerState STATE = new PlayerState(TICKETS, CARDS, ROUTES);
    
    @Test
    void initialFails() {
        assertThrows(IllegalArgumentException.class, () -> PlayerState.initial(SortedBag.of()));
        assertThrows(IllegalArgumentException.class, () -> PlayerState.initial(SortedBag.of(
                List.of(Card.GREEN, Card.LOCOMOTIVE, Card.BLUE)
        )));
    }
    
    @Test
    void initialWorks() {
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
        assertEquals(TICKETS, STATE.tickets());
    }

    @Test
    void withAddedTickets() {
        PlayerState newState = STATE.withAddedTickets(ADDED_TICKETS);
        assertEquals(TICKETS.union(ADDED_TICKETS), newState.tickets());
        assertEquals(
                TICKETS.size() + ADDED_TICKETS.size(),
                newState.tickets().size()
        );
        assertEquals(TICKETS, STATE.tickets());
    }

    @Test
    void cards() {
        assertEquals(CARDS, STATE.cards());
    }

    @Test
    void withAddedCard() {
        PlayerState newState = STATE.withAddedCard(Card.GREEN);
        assertEquals(
                CARDS.union(SortedBag.of(Card.GREEN)),
                newState.cards()
        );
        assertEquals(
                CARDS.size() + 1,
                newState.cards().size()
        );
        assertEquals(CARDS, STATE.cards());
    }

    @Test
    void canClaimRoute() {
        final Route LONG_ROUTE = ChMap.routes().get(22);
        List<Route> routes = Collections.nCopies(6, LONG_ROUTE);
        PlayerState notEnoughCar = new PlayerState(TICKETS, SortedBag.of(), routes);
        
        assertFalse(notEnoughCar.canClaimRoute(LONG_ROUTE));
    
        final Route PITITE_ROUTE = ChMap.routes().get(2);
        final SortedBag<Card> CARDS2 = SortedBag.of(5, Card.LOCOMOTIVE, 5, Card.RED);
        PlayerState state2 = new PlayerState(TICKETS, CARDS2, routes);
        PlayerState state3 = new PlayerState(TICKETS, SortedBag.of(), routes);
        
        assertTrue(state2.canClaimRoute(PITITE_ROUTE));
        assertFalse(state3.canClaimRoute(PITITE_ROUTE));
    }

    @Test
    void possibleClaimCardsFails() {
        final Route LONG_ROUTE = ChMap.routes().get(22);
        List<Route> routes = Collections.nCopies(6, LONG_ROUTE);
        PlayerState state = new PlayerState(TICKETS, CARDS, routes);
        assertThrows(IllegalArgumentException.class, () -> state.possibleClaimCards(LONG_ROUTE));
    }
    
    @Test
    void possibleClaimCardsWorks() {
        final Route LONG_ROUTE = ChMap.routes().get(22);
        List<Route> routes = Collections.nCopies(5, LONG_ROUTE);
        PlayerState state = new PlayerState(TICKETS, CARDS, routes);
        List<SortedBag<Card>> possible = state.possibleClaimCards(LONG_ROUTE);
        assertEquals(
                List.of(CARDS), possible
        );
        
        assertThrows(UnsupportedOperationException.class, possible::clear);
    
        final Route PITITE_ROUTE = ChMap.routes().get(2);
        final SortedBag<Card> CARDS2 = SortedBag.of(5, Card.LOCOMOTIVE, 5, Card.RED);
        PlayerState state2 = new PlayerState(TICKETS, CARDS2, routes);
        List<SortedBag<Card>> expected = List.of(
                SortedBag.of(3, Card.RED),
                SortedBag.of(2, Card.RED, 1, Card.LOCOMOTIVE),
                SortedBag.of(1, Card.RED, 2, Card.LOCOMOTIVE),
                SortedBag.of(3, Card.LOCOMOTIVE)
        );
        List<SortedBag<Card>> possible2 = state2.possibleClaimCards(PITITE_ROUTE);
        assertEquals(
                expected, possible2
        );
    
        final Route OVER_ROUTE = ChMap.routes().get(3);
        final SortedBag<Card> CARDS3 = SortedBag.of(5, Card.VIOLET);
        PlayerState state3 = new PlayerState(TICKETS, CARDS3, routes);
        List<SortedBag<Card>> possible3 = state3.possibleClaimCards(OVER_ROUTE);
        assertEquals(
                List.of(SortedBag.of(2, Card.VIOLET)), possible3
        );
    
        final SortedBag<Card> CARDS4 = SortedBag.of(1, Card.VIOLET);
        PlayerState state4 = new PlayerState(TICKETS, CARDS4, routes);
        List<SortedBag<Card>> possible4 = state4.possibleClaimCards(OVER_ROUTE);
        assertEquals(
                List.of(), possible4
        );
    }

    @Test
    void possibleAdditionalCardsFails() {
        PlayerState state = new PlayerState(TICKETS, CARDS, ROUTES);
        assertThrows(IllegalArgumentException.class, () -> {
            state.possibleAdditionalCards(0, SortedBag.of(Card.RED));
        });
        assertThrows(IllegalArgumentException.class, () -> {
            state.possibleAdditionalCards(4, SortedBag.of(Card.RED));
        });
    
        assertThrows(IllegalArgumentException.class, () -> {
            state.possibleAdditionalCards(2, SortedBag.of());
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            state.possibleAdditionalCards(2, CARDS.union(SortedBag.of(Card.GREEN)));
        });
    }
    
    @Test
    void possibleAdditionalCardsWorks() {
        SortedBag<Card> exampleCards1 = 
                SortedBag.of(3, Card.GREEN, 2, Card.BLUE)
                        .union(SortedBag.of(2, Card.LOCOMOTIVE));
        
        final PlayerState STATE1 = new PlayerState(TICKETS, exampleCards1, ROUTES);
        
        assertEquals(
                List.of(
                        SortedBag.of(2, Card.GREEN),
                        SortedBag.of(1, Card.GREEN, 1, Card.LOCOMOTIVE),
                        SortedBag.of(2, Card.LOCOMOTIVE)
                ),
                STATE1.possibleAdditionalCards(2, SortedBag.of(Card.GREEN))
        );
        
        SortedBag<Card> exampleCards2 = SortedBag.of(3, Card.YELLOW, 2, Card.WHITE);
        
        final PlayerState STATE2 = new PlayerState(TICKETS, exampleCards2, ROUTES);
        
        assertEquals(
                List.of(
                        SortedBag.of(Card.YELLOW)
                ),
                STATE2.possibleAdditionalCards(1, SortedBag.of(Card.YELLOW))
        );
    
        SortedBag<Card> exampleCards3 = SortedBag.of(5, Card.ORANGE, 1, Card.LOCOMOTIVE);
    
        final PlayerState STATE3 = new PlayerState(TICKETS, exampleCards3, ROUTES);
    
        assertEquals(
                List.of(
                        SortedBag.of(3, Card.ORANGE),
                        SortedBag.of(2, Card.ORANGE, 1, Card.LOCOMOTIVE)
                ),
                STATE3.possibleAdditionalCards(3, SortedBag.of(2, Card.ORANGE))
        );
    
        SortedBag<Card> exampleCards4 = SortedBag.of(5, Card.GREEN);
    
        final PlayerState STATE4 = new PlayerState(TICKETS, exampleCards4, ROUTES);
    
        assertEquals(
                List.of(),
                STATE4.possibleAdditionalCards(3, SortedBag.of(3, Card.GREEN))
        );
    }

    @Test
    void withClaimedRoute() {
        Route newRoute = ChMap.routes().get(83);
        PlayerState newState = STATE.withClaimedRoute(newRoute, SortedBag.of(4, Card.BLACK));
        
        List<Route> newExpectedRoutes = new ArrayList<>(ROUTES);
        newExpectedRoutes.add(newRoute);
        SortedBag<Card> newExpectedCards = SortedBag.of(1, Card.BLACK, 1, Card.LOCOMOTIVE);
        
        assertEquals(
                newExpectedRoutes,
                newState.routes()
        );
        assertEquals(
                newExpectedCards,
                newState.cards()
        );
        assertEquals(
                TICKETS,
                newState.tickets()
        );
    }

    @Test
    void ticketPoints() {
        SortedBag<Ticket> testTickets1 = SortedBag.of(ChMap.tickets().subList(14, 16));
        List<Route> testRoutes1 = List.of(
                ChMap.routes().get(44), ChMap.routes().get(13), ChMap.routes().get(15)
        );
        
        PlayerState state1 = new PlayerState(testTickets1, CARDS, testRoutes1);
        assertEquals(
                7 - 8, state1.ticketPoints()
        );
        
        SortedBag<Ticket> testTickets2 = SortedBag.of(ChMap.tickets().get(34));
        List<Route> testRoutes2 = List.of(ChMap.routes().get(18), ChMap.routes().get(57), ChMap.routes().get(43));
        
        PlayerState state2 = new PlayerState(testTickets2, CARDS, testRoutes2);
        assertEquals(
                5, state2.ticketPoints()
        );
    
        SortedBag<Ticket> testTickets3 = SortedBag.of(1, ChMap.tickets().get(34), 1, ChMap.tickets().get(35));
        List<Route> testRoutes3 = List.of(ChMap.routes().get(18), ChMap.routes().get(57), ChMap.routes().get(43));
    
        PlayerState state3 = new PlayerState(testTickets3, CARDS, testRoutes3);
        assertEquals(
                5 - 3, state3.ticketPoints()
        );
    }

    @Test
    void finalPoints() {
        SortedBag<Ticket> testTickets1 = SortedBag.of(ChMap.tickets().subList(14, 16));
        List<Route> testRoutes1 = List.of(
                ChMap.routes().get(44), ChMap.routes().get(13), ChMap.routes().get(15)
        );
        int claimPoints1 = 1 + 4 + 4;
    
        PlayerState state1 = new PlayerState(testTickets1, CARDS, testRoutes1);
        assertEquals(
                7 - 8 + claimPoints1, state1.finalPoints()
        );
    
        SortedBag<Ticket> testTickets2 = SortedBag.of(ChMap.tickets().get(34));
        List<Route> testRoutes2 = List.of(ChMap.routes().get(18), ChMap.routes().get(57), ChMap.routes().get(43));
        int claimPoints2 = 2 + 1 + 2;
        
        PlayerState state2 = new PlayerState(testTickets2, CARDS, testRoutes2);
        assertEquals(
                5 + claimPoints2, state2.finalPoints()
        );
    
        SortedBag<Ticket> testTickets3 = SortedBag.of(1, ChMap.tickets().get(34), 1, ChMap.tickets().get(35));
        List<Route> testRoutes3 = List.of(ChMap.routes().get(18), ChMap.routes().get(57), ChMap.routes().get(43));
        int claimPoints3 = 2 + 1 + 2;
    
        PlayerState state3 = new PlayerState(testTickets3, CARDS, testRoutes3);
        assertEquals(
                5 - 3 + claimPoints3, state3.finalPoints()
        );
    }
    
}