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
    
    private static final StationPartition.Builder BUILDER = new StationPartition.Builder(
            getMaxId(ROUTES) + 1
    );
    
    private StationPartition partition;
    
    private void connectRoutes(List<Route> routes) {
        for (Route r : routes) {
            BUILDER.connect(r.station1(), r.station2());
        }
        partition = BUILDER.build();
    }

    @Test
    void testStationPartition() {
        connectRoutes(ROUTES);
        
        for (Station s1 : STATIONS) {
            String temp = "";
            for (Station s2 : STATIONS) {
                if (partition.connected(s1, s2)) {
                    temp = s2.name();
                    break;
                }
            }
            System.out.printf("%12s et %12s sont connectées\n", s1.name(), temp);
        }
    }
    
    @Test
    void connectedWorks() {
        connectRoutes(ROUTES);
        
        assertFalse(
                partition.connected(getStation(3), getStation(0))
        );
        assertTrue(
                partition.connected(getStation(13), getStation(9))
        );
    }
    
}