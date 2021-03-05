package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RouteTestMass {
    private static final Station AT1 = new Station(39, "Autriche");
    private static final Station STG = new Station(27, "Saint-Gall");
    private static final Station AT2 = new Station(40, "Autriche");
    private static final Station VAD = new Station(28, "Vaduz");
    private static final Station BER = new Station(3, "Berne");
    private static final Station FRI = new Station(9, "Fribourg");
    private static final Station LUC = new Station(16, "Lucerne");
    private static final Station GEN = new Station(10, "Genève");
    private static final Station YVE = new Station(31, "Yverdon");
    @Test
    void RouteWorkWithNullColor(){
        Route route = new Route("random id", new Station (2, "name1"),new Station (5, "name4"),4,  Route.Level.OVERGROUND, null );
        assertEquals(null, route.color());
    }

    //this doesn't work properly (comparison of memory adress of exact same object)
    /*@Test
    void RouteWorkWithCOrrectOrderForStations(){
        Route route = new Route("random id", new Station (2, "name1"),new Station (5, "name4"),4,  Route.Level.OVERGROUND, null );
        ArrayList<Station> arrayOfStation = new ArrayList<Station>();
        arrayOfStation.add(new Station (2, "name1"));
        arrayOfStation.add(new Station (5, "name4"));
        assertEquals(arrayOfStation, route.stations());
    }*/
    
    @Test
    void StationOppositeThrowException(){
        assertThrows( IllegalArgumentException.class,() -> {
            Route route = new Route("random id", new Station (2, "name1"),new Station (5, "name4"),4,  Route.Level.OVERGROUND, null );
            route.stationOpposite(new Station(3, "name4"));
        });
    }

    @Test
    void OppositeStationReturnOppositeStation(){
        Station station = new Station (5, "name4");
        Station station2 = new Station (5, "name4");
        Route route = new Route("random id", station, station2,4,  Route.Level.OVERGROUND, null );
        assertEquals(route.stationOpposite(station), station2);
    }
    @Test
    void PosssibleCardsClaimsCountThrowsExceptionWithNoTunnel(){
        Station station = new Station (5, "name4");
        Station station2 = new Station (5, "name4");
        Route route = new Route("random id", station, station2,4,  Route.Level.OVERGROUND, null );
        route.possibleClaimCards();
    }




    @Test
    void constructorFailsWithNullStations() {
        assertThrows(NullPointerException.class, () -> {
            new Route("BER_FRI_1", null, FRI, 1, Route.Level.OVERGROUND, Color.ORANGE);
        });
        assertThrows(NullPointerException.class, () -> {
            new Route("BER_FRI_1", BER, null, 1, Route.Level.OVERGROUND, Color.ORANGE);
        });
    }
    @Test
    void constructorFailsWithSameStation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Route("BER_FRI_1", FRI, FRI, 1, Route.Level.OVERGROUND, Color.ORANGE);
        });
    }
    @Test
    void constructorFailsWithLessOrMorePoints() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Route("BER_FRI_1", BER, FRI, 7, Route.Level.OVERGROUND, Color.ORANGE);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Route("BER_FRI_1", BER, FRI, -1, Route.Level.OVERGROUND, Color.ORANGE);
        });
    }
    @Test
    void constructorFailsWithNullLevel() {
        assertThrows(NullPointerException.class, () -> {
            new Route("BER_FRI_1", BER, FRI, 6, null, Color.ORANGE);
        });
    }
    @Test
    void idOfRouteFails() {
        Route route = new Route("BER_FRI_1", BER, FRI, 6, Route.Level.OVERGROUND, Color.ORANGE);
        assertEquals("BER_FRI_1", route.id());
    }
    @Test
    void stationsOfRouteFails() {
        Route route = new Route("BER_FRI_1", BER, FRI, 6, Route.Level.OVERGROUND, Color.ORANGE);
        assertEquals(BER, route.station1());
        assertEquals(FRI, route.station2());
    }
    @Test
    void listOfStationsFails() {
        List stations = new ArrayList<Route>();
        Route route = new Route("BER_FRI_1", BER, FRI, 6, Route.Level.OVERGROUND, null);
        stations.add(BER);
        stations.add(FRI);
        assertEquals(stations.get(0), route.stations().get(0));
        assertEquals(stations.get(1), route.stations().get(1));
    }
    @Test
    void stationOppositeFails() {
        Route route = new Route("BER_FRI_1", BER, FRI, 6, Route.Level.OVERGROUND, Color.ORANGE);
        assertEquals(FRI, route.stationOpposite(BER));
        assertEquals(BER, route.stationOpposite(FRI));
    }
    @Test
    void drawnCardsMustBeThree() {
        Route route = new Route("AT1_STG_1", AT1, STG, 4, Route.Level.UNDERGROUND, Color.ORANGE);
        SortedBag claimCards = SortedBag.of(1, Card.CARS.get(1));
        SortedBag drawnCards = SortedBag.of(5, Card.CARS.get(0));
        assertThrows(IllegalArgumentException.class, () -> {
            route.additionalClaimCardsCount(claimCards,drawnCards);
        });
    }
    @Test
    void mustBeATunnel() {
        Route route = new Route("AT1_STG_1", AT1, STG, 4, Route.Level.OVERGROUND, Color.ORANGE);
        SortedBag claimCards = SortedBag.of(1, Card.CARS.get(1));
        SortedBag drawnCards = SortedBag.of(3, Card.CARS.get(0));
        assertThrows(IllegalArgumentException.class, () -> {
            route.additionalClaimCardsCount(claimCards,drawnCards);
        });
    }
    @Test
    void claimNoCards() {
        Route route = new Route("AT1_STG_1", AT1, STG, 4, Route.Level.UNDERGROUND, Color.ORANGE);
        SortedBag claimCards = SortedBag.of(1, Card.CARS.get(1));
        SortedBag drawnCards = SortedBag.of(3, Card.CARS.get(2));
        assertEquals(0, route.additionalClaimCardsCount(claimCards,drawnCards));
    }
    @Test
    void claimBecauseLocomotive() {
        Route route = new Route("AT1_STG_1", AT1, STG, 4, Route.Level.UNDERGROUND, Color.ORANGE);
        SortedBag claimCards = SortedBag.of(1, Card.CARS.get(1));
        SortedBag drawnCards0 = SortedBag.of(2, Card.CARS.get(2));
        SortedBag drawnCards1 = SortedBag.of(1, Card.ALL.get(8));
        SortedBag drawnCards = drawnCards0.union(drawnCards1);
        assertEquals(1, route.additionalClaimCardsCount(claimCards,drawnCards));
    }
    @Test
    void claimBecauseLocomotiveAndColor() {
        Route route = ChMap.routes().get(120 - 92);
        SortedBag claimCards = SortedBag.of(1, Card.of(Color.VIOLET));
        SortedBag drawnCards0 = SortedBag.of(2, Card.of(Color.VIOLET));
        SortedBag drawnCards1 = SortedBag.of(1, Card.LOCOMOTIVE);
        SortedBag drawnCards = drawnCards0.union(drawnCards1);
        assertEquals(3, route.additionalClaimCardsCount(claimCards,drawnCards));
    }
    @Test
    void possibleClaimCardTestTrivial() {
        Route route = new Route("AT1_STG_1", AT1, STG, 2, Route.Level.UNDERGROUND, null);
        assertEquals("[{2×BLACK}, {2×VIOLET}, {2×BLUE}, {2×GREEN}, {2×YELLOW}, {2×ORANGE}, {2×RED}, {2×WHITE}, {BLACK, LOCOMOTIVE}, {VIOLET, LOCOMOTIVE}, {BLUE, LOCOMOTIVE}, {GREEN, LOCOMOTIVE}, {YELLOW, LOCOMOTIVE}, {ORANGE, LOCOMOTIVE}, {RED, LOCOMOTIVE}, {WHITE, LOCOMOTIVE}, {2×LOCOMOTIVE}]"
                , Arrays.toString(route.possibleClaimCards().toArray()));
    }
    @Test
    void claimPointsTest() {
        
    }
    
}
