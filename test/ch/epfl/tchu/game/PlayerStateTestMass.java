package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PlayerStateTestMass {
    List<Station> from = List.of(
            new Station(0, "Lausanne"),
            new Station(1, "Neuchâtel"));
    List<Station> to = List.of(
            new Station(2, "Berne"),
            new Station(3, "Zürich"),
            new Station(4, "Coire"));
    int points = 17;
    Ticket trivialTicket1 = new Ticket(List.of(new Trip(from.get(0), to.get(0), points)));
    Ticket trivialTicket2 = new Ticket(List.of(new Trip(from.get(1), to.get(1), points)));
    Ticket trivialTicket3 = new Ticket(List.of(new Trip(from.get(1), to.get(2), points)));
    Ticket trivialTicket4 = new Ticket(List.of(new Trip(from.get(1), to.get(2), points)));
    Station station0 = new Station(0, "Station0");
    Station station1 = new Station(1, "Station1");
    Station station2 = new Station(2, "Station2");
    Station station3 = new Station(3, "Station3");
    Route tunnel1 = new Route("Tunnel", station0, station1, 2, Route.Level.UNDERGROUND, Color.BLACK);
    Route tunnel2 = new Route("Tunnel", station0, station1, 2, Route.Level.UNDERGROUND, null);
    Route tunnel21 = new Route("Tunnel", station0, station1, 2, Route.Level.UNDERGROUND, null);
    Route tunnel3 = new Route("Tunnel", station0, station1, 4, Route.Level.UNDERGROUND, null);
    Route tunnel4 = new Route("Tunnel", station0, station1, 3, Route.Level.UNDERGROUND, Color.BLUE);
    Route route1 = new Route("R", station0, station1, 2, Route.Level.OVERGROUND, Color.ORANGE);
    Route route2 = new Route("R", station0, station1, 5, Route.Level.OVERGROUND, null);
    Route route3 = new Route("R", station0, station1, 2, Route.Level.OVERGROUND, null);
    Route route4 = new Route("R", station0, station1, 4, Route.Level.OVERGROUND, Color.ORANGE);

    @Test
    void ticketsWorks(){
        PlayerState trivialCase = new PlayerState(
                SortedBag.of(1, trivialTicket1, 1, trivialTicket2),
                SortedBag.of(5, Card.ORANGE, 2, Card.BLUE),
                List.of(route1,route2));
        assertEquals(2, trivialCase.tickets().size());
        assertEquals(SortedBag.of(1, trivialTicket1, 1, trivialTicket2), trivialCase.tickets());
        //Add Tickets
        SortedBag.Builder<Ticket> sBuilder = new SortedBag.Builder<Ticket>();
        sBuilder.add(trivialTicket1);
        sBuilder.add(trivialTicket3);
        sBuilder.add(trivialTicket2);
        assertEquals(sBuilder.build(), trivialCase.withAddedTickets(SortedBag.of(1, trivialTicket3, 1, trivialTicket4)).tickets());
    }
    @Test
    void cardsWorks(){
        PlayerState trivialCase = new PlayerState(
                SortedBag.of(1, trivialTicket1, 1, trivialTicket2),
                SortedBag.of(5, Card.ORANGE, 2, Card.BLUE),
                List.of(route1,route2));
        assertEquals(7, trivialCase.cards().size());
        assertEquals(SortedBag.of(5, Card.ORANGE, 2, Card.BLUE), trivialCase.cards());
        //Add Cards
        SortedBag.Builder<Card> sBuilder = new SortedBag.Builder<Card>();
        sBuilder.add(5, Card.ORANGE);
        sBuilder.add(2, Card.BLUE);
        sBuilder.add(1, Card.LOCOMOTIVE);
        assertEquals(sBuilder.build(), trivialCase.withAddedCard(Card.LOCOMOTIVE).cards());
        assertEquals(sBuilder.build().union(SortedBag.of(3, Card.GREEN, 2, Card.RED)), trivialCase.withAddedCards(SortedBag.of(3, Card.GREEN, 2, Card.RED)).cards().union(SortedBag.of(Card.LOCOMOTIVE)));
    }
    @Test
    void canClaimRouteWorks(){
        SortedBag.Builder<Card> sBuilder = new SortedBag.Builder<Card>();
        sBuilder.add(2, Card.ORANGE);
        sBuilder.add(2, Card.BLUE);
        sBuilder.add(1, Card.LOCOMOTIVE);
        PlayerState trivialCase = new PlayerState(
                SortedBag.of(1, trivialTicket1, 1, trivialTicket2),
                sBuilder.build(),
                List.of(route1,route2));
        assertEquals(true, trivialCase.canClaimRoute(route1));
        assertEquals(false, trivialCase.canClaimRoute(route2));
        assertEquals(true, trivialCase.canClaimRoute(route3));
        assertEquals(false, trivialCase.canClaimRoute(route4));

        assertEquals(false, trivialCase.canClaimRoute(tunnel1));
        assertEquals(true, trivialCase.canClaimRoute(tunnel2));
        assertEquals(false, trivialCase.canClaimRoute(tunnel3));
        assertEquals(true, trivialCase.canClaimRoute(tunnel4));
        PlayerState onlyLoco = new PlayerState(
                SortedBag.of(1, trivialTicket1, 1, trivialTicket2),
                SortedBag.of(5, Card.LOCOMOTIVE),
                List.of(route1,route2));
        assertEquals(false, onlyLoco.canClaimRoute(route1));
        assertEquals(false, onlyLoco.canClaimRoute(route2));
        assertEquals(false, onlyLoco.canClaimRoute(route3));
        assertEquals(false, onlyLoco.canClaimRoute(route4));
        assertEquals(true, onlyLoco.canClaimRoute(tunnel1));
        assertEquals(true, onlyLoco.canClaimRoute(tunnel2));
        assertEquals(true, onlyLoco.canClaimRoute(tunnel3));
        assertEquals(true, onlyLoco.canClaimRoute(tunnel4));
        PlayerState emptyCase = new PlayerState(
                SortedBag.of(1, trivialTicket1, 1, trivialTicket2),
                SortedBag.of(),
                List.of(route1,route2));
        assertEquals(false, emptyCase.canClaimRoute(route1));
        assertEquals(false, emptyCase.canClaimRoute(route2));
        assertEquals(false, emptyCase.canClaimRoute(route3));
        assertEquals(false, emptyCase.canClaimRoute(route4));
        assertEquals(false, emptyCase.canClaimRoute(tunnel1));
        assertEquals(false, emptyCase.canClaimRoute(tunnel2));
        assertEquals(false, emptyCase.canClaimRoute(tunnel3));
        assertEquals(false, emptyCase.canClaimRoute(tunnel4));

    }
    @Test
    void possibleClaimWorks(){ SortedBag.Builder<Card> sBuilder = new SortedBag.Builder<Card>();
        sBuilder.add(2, Card.ORANGE);
        sBuilder.add(2, Card.BLUE);
        sBuilder.add(0, Card.LOCOMOTIVE);
        PlayerState emptyCase = new PlayerState(
                SortedBag.of(1, trivialTicket1, 1, trivialTicket2),
                SortedBag.of(),
                List.of(route1,route2));
        assertThrows(IllegalArgumentException.class, () -> {
            emptyCase.possibleClaimCards(tunnel1);
        });

        PlayerState trivialCase = new PlayerState(
                SortedBag.of(1, trivialTicket1, 1, trivialTicket2),
                sBuilder.build(),
                List.of(route1,route2));
        assertThrows(IllegalArgumentException.class, () -> {
            trivialCase.possibleClaimCards(tunnel1);
        });
        assertEquals(List.of(SortedBag.of(2, Card.ORANGE)), trivialCase.possibleClaimCards(route1));
        assertEquals(List.of(SortedBag.of(2, Card.BLUE), SortedBag.of(2, Card.ORANGE)), trivialCase.possibleClaimCards(route3));
        assertEquals(List.of(SortedBag.of(2, Card.BLUE), SortedBag.of(2, Card.ORANGE)), trivialCase.possibleClaimCards(tunnel21));
    }
    @Test
    void possibleAdditionalCardsWorks(){
        SortedBag.Builder<Card> sBuilder = new SortedBag.Builder<Card>();
        sBuilder.add(10, Card.RED);
        sBuilder.add(10, Card.BLUE);
        sBuilder.add(10, Card.LOCOMOTIVE);
        PlayerState trivialCase = new PlayerState(
                SortedBag.of(1, trivialTicket1, 1, trivialTicket2),
                sBuilder.build(),
                List.of(route1,route2));
        assertThrows(IllegalArgumentException.class, () -> {
            trivialCase.possibleAdditionalCards(1, SortedBag.of(2, Card.LOCOMOTIVE), SortedBag.of(2, Card.LOCOMOTIVE, 1, Card.BLUE));
        });
        assertThrows(IllegalArgumentException.class, () -> {
            trivialCase.possibleAdditionalCards(2, SortedBag.of(), SortedBag.of(2, Card.LOCOMOTIVE, 1, Card.BLUE));
        });
        assertThrows(IllegalArgumentException.class, () -> {
            trivialCase.possibleAdditionalCards(2, sBuilder.build(), SortedBag.of(2, Card.LOCOMOTIVE, 1, Card.BLUE));
        });
        assertThrows(IllegalArgumentException.class, () -> {
            trivialCase.possibleAdditionalCards(2, SortedBag.of(2, Card.LOCOMOTIVE), SortedBag.of(2, Card.LOCOMOTIVE, 2, Card.BLUE));
        });
        List<Card> cardsNeeded = new ArrayList<Card>(sBuilder.build().toSet());
        assertEquals(List.of(SortedBag.of(3, Card.RED)), trivialCase.possibleAdditionalCards(3, SortedBag.of(2, Card.RED), SortedBag.of(3, Card.RED)));
        assertEquals(List.of(SortedBag.of(3, Card.RED)), trivialCase.possibleAdditionalCards(3, SortedBag.of(2, Card.RED, 1, Card.LOCOMOTIVE), SortedBag.of(3, Card.RED)));
        assertEquals(List.of(SortedBag.of(2, Card.RED)), trivialCase.possibleAdditionalCards(2, SortedBag.of(2, Card.RED, 1, Card.LOCOMOTIVE), SortedBag.of(2, Card.RED, 1, Card.BLACK)));
        assertEquals(List.of(SortedBag.of(1, Card.RED, 2, Card.LOCOMOTIVE), SortedBag.of(3, Card.LOCOMOTIVE)), trivialCase.possibleAdditionalCards(3, SortedBag.of(2, Card.RED, 1, Card.LOCOMOTIVE), SortedBag.of(1, Card.RED, 2, Card.LOCOMOTIVE)));
        assertEquals(List.of(), trivialCase.possibleAdditionalCards(3, SortedBag.of(2, Card.RED, 1, Card.LOCOMOTIVE), SortedBag.of(3, Card.BLUE)));
    }
    @Test
    void withAddedTickets(){
        SortedBag<Ticket> sortedBagTest1 = SortedBag.of(ChMap.tickets());
        SortedBag<Card>sortedBagCards = SortedBag.of(4,Card.BLUE,5,Card.BLACK);
        SortedBag<Card>correctSortedBag = SortedBag.of(2,Card.BLUE,2,Card.BLACK);
        PlayerState playerState3 = new PlayerState(SortedBag.of(),correctSortedBag, new ArrayList<Route>());
        playerState3.withAddedTickets(sortedBagTest1);
        PlayerState playerState = new PlayerState(sortedBagTest1.union(SortedBag.of()), sortedBagCards, ChMap.routes());
        assertEquals(playerState.tickets(),playerState.tickets());
    }
}
