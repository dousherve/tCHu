package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StationPartitionTest {
    
    private static Route fromLine(int line) { return ChMap.routes().get(line - 92); }
    private static Station getStation(int index) { return ChMap.stations().get(index); }
    
    private static int getMaxId(List<Route> routes) {
        int maxStationId = 0;
        for (Route r : routes)
            for (Station s : r.stations())
                maxStationId = Math.max(maxStationId, s.id());
            
        return maxStationId;
    }
    
    private static final List<Route> ROUTES = List.of(
            fromLine(136), fromLine(105), fromLine(94)
    );
    
    private static final List<Station> STATIONS = ChMap.stations().subList(0, getMaxId(ROUTES) + 1);
    
    // Tests gares françaises
    
    private static final Station PAR = new Station(0, "Paris");
    private static final Station LYO = new Station(1, "Lyon");
    private static final Station BDX = new Station(2, "Bordeaux");
    private static final Station MAR = new Station(3, "Marseille");
    private static final Station TLS = new Station(4, "Toulouse");
    private static final Station NIC = new Station(5, "Nice");
    private static final Station BRE = new Station(6, "Brest");
    private static final Station NAN = new Station(7, "Nantes");
    
    private static final Route PAR_NIC = new Route("PAR_NIC", PAR, NIC, 2, Route.Level.OVERGROUND, Color.BLUE);
    private static final Route PAR_BRE = new Route("PAR_BRE", PAR, BRE, 2, Route.Level.OVERGROUND, Color.BLUE);
    private static final Route BDX_NAN = new Route("BDX_NAN", BDX, NAN, 2, Route.Level.OVERGROUND, Color.BLUE);
    private static final Route NAN_NIC = new Route("NAN_NIC", NAN, NIC, 2, Route.Level.OVERGROUND, Color.BLUE);
    private static final Route MAR_LYO = new Route("MAR_LYO", MAR, LYO, 2, Route.Level.OVERGROUND, Color.BLUE);

    @Test
    void testStationPartition() {
        StationPartition.Builder builder = new StationPartition.Builder(getMaxId(ROUTES) + 1);
        for (Route r : ROUTES)
            builder.connect(r.station1(), r.station2());
        
        StationPartition partition = builder.build();
    
        List<Station> subset1 = List.of(getStation(0), getStation(1));
        List<Station> subset2 = List.of(getStation(3), getStation(9), getStation(13));
        
        for (Station s : STATIONS)
            assertTrue(partition.connected(s, s));
    
        for (Station s1 : STATIONS) {
            for (Station s2 : subset1) {
                if (subset1.contains(s1))
                    assertTrue(partition.connected(s1, s2));
                else
                    assertFalse(partition.connected(s1, s2));
            }
            for (Station s2 : subset2) {
                if (subset2.contains(s1))
                    assertTrue(partition.connected(s1, s2));
                else
                    assertFalse(partition.connected(s1, s2));
            }
        }
    }
    
    @Test
    void testFrenchStations() {
        List<Route> routes = List.of(
                PAR_NIC,
                PAR_BRE,
                BDX_NAN,
                NAN_NIC,
                MAR_LYO
        );
        
        List<Station> stations = List.of(
                PAR,
                LYO,
                BDX,
                MAR,
                TLS,
                NIC,
                BRE,
                NAN
        );
        
        List<Station> subset1 = List.of(PAR, NAN, BDX, BRE, NIC);
        List<Station> subset2 = List.of(LYO, MAR);
        
        StationPartition.Builder builder = new StationPartition.Builder(getMaxId(routes) + 1);
        for (Route r : routes)
            builder.connect(r.station1(), r.station2());
        
        StationPartition partition = builder.build();
        
        for (Station s : stations)
            assertTrue(partition.connected(s, s));
            
        for (Station s1 : stations) {
            for (Station s2 : subset1) {
                if (subset1.contains(s1))
                    assertTrue(partition.connected(s1, s2));
                else
                    assertFalse(partition.connected(s1, s2));
            }
            for (Station s2 : subset2) {
                if (subset2.contains(s1))
                    assertTrue(partition.connected(s1, s2));
                else
                    assertFalse(partition.connected(s1, s2));
            }
        }
    }
    
    @Test
    void connectedWorks() {
        StationPartition.Builder builder = new StationPartition.Builder(getMaxId(ROUTES) + 1);
        
        for (Route r : ROUTES)
            builder.connect(r.station1(), r.station2());
        
        StationPartition partition = builder.build();
        
        assertFalse(
                partition.connected(getStation(3), getStation(0))
        );
        assertTrue(
                partition.connected(getStation(13), getStation(9))
        );
        
        assertTrue(partition.connected(ChMap.stations().get(50), ChMap.stations().get(50)));
        assertFalse(partition.connected(ChMap.stations().get(50), ChMap.stations().get(49)));
        assertFalse(partition.connected(ChMap.stations().get(2), ChMap.stations().get(49)));
    }
    
}