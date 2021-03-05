package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrailTestJulia {

    Station YVE = new Station(31, "Yverdon");
    Station SOL = new Station(26, "Soleure");
    Station NEU = new Station(19, "Neuchâtel");
    Station FRI = new Station(9, "Fribourg");
    Station BER = new Station(3, "Berne");
    Station LAU = new Station(13, "Lausanne");
    Route BERFRI = new Route("BER_FRI_2",BER, FRI, 1,Route.Level.OVERGROUND, Color.YELLOW);
    Route FRILAU = new Route("FRI_LAU_1",FRI, LAU, 3,Route.Level.OVERGROUND, Color.RED);
    Route NEUYVE= new Route("NEU_YVE_1",NEU, YVE, 2,Route.Level.OVERGROUND, Color.BLACK);
    Route NEUSOL=new Route("NEU_SOL_1",NEU, SOL, 4,Route.Level.OVERGROUND, Color.GREEN);
    Route LAUNEU=new Route("LAU_NEU_1",LAU, NEU, 4,Route.Level.OVERGROUND, null);
    
    // ======= Ses gares sont les opposées des notres =======
    
    @Test
    void longestWorksWithEmptyList(){
        List<Route> list = new ArrayList<>();
        int expectedValue=0;
        int actualValue= Trail.longest(list).length();
        assertEquals(expectedValue, actualValue);
    }

    @Test
    void longestWorksWithNonEmptyList(){
        List<Route> list = new ArrayList<>();
        list.add(NEUYVE);
        list.add(NEUSOL);
        list.add(LAUNEU);
        list.add(FRILAU);
        list.add(BERFRI);
        int expectedValue=12;
        int actualValue=Trail.longest(list).length();
        assertEquals(expectedValue, actualValue);
    }

    @Test
    void longuestWithNonLinkedList(){
        List<Route> list = new ArrayList<>();
        list.add(NEUYVE);
        list.add(FRILAU);
        int expectedValue=3;
        int actualValue=Trail.longest(list).length();
        assertEquals(expectedValue, actualValue);
    }

    @Test
    void lengthWorksWithEmptyList(){
        List<Route> list = new ArrayList<>();
        assertEquals(0,Trail.longest(list).length());
    }

    @Test
    void lengthWorksWithNonEmptyList() {
        List<Route> list = new ArrayList<>();
        list.add(FRILAU);
        list.add(BERFRI);
        list.add(LAUNEU);
        int expectedValue = 8;
        int actualValue = Trail.longest(list).length();
        assertEquals(expectedValue, actualValue);
    }


    @Test
    void station1WithNullStation(){
        List<Route> list = new ArrayList<>();
        Trail trail = Trail.longest(list);
        Station actualStation = trail.station2();
        assertEquals(null,actualStation);
    }

    @Test
    void station2WithNullStation(){
        List<Route> list = new ArrayList<>();
        Trail trail = Trail.longest(list);
        Station actualStation = trail.station1();
        assertEquals(null,actualStation);
    }


    @Test
    void station1WithLength1(){
        Trail trail = Trail.longest(List.of(NEUSOL,NEUYVE));
        Station expectedStation1 = YVE;
        Station actualStation = trail.station2();
        assertEquals(expectedStation1, actualStation);
    }


    @Test
    void station2WithLength1(){
        Trail trail =Trail.longest(List.of(NEUSOL,NEUYVE));
        Station expectedStation = SOL;
        Station actualStation = trail.station1();
        assertEquals(expectedStation, actualStation);
    }

    @Test
    void station1WithLongTrail(){
        List<Route> list = new ArrayList<>();
        list.add(FRILAU);
        list.add(BERFRI);
        list.add(LAUNEU);
        Trail trail = Trail.longest(list);
        Station expectedStation = NEU;
        Station actualStation = trail.station2();
        assertEquals(expectedStation, actualStation);
    }

    @Test
    void station2WithLongTrail(){
        List<Route> list = new ArrayList<>();
        list.add(FRILAU);
        list.add(BERFRI);
        list.add(LAUNEU);
        Trail trail = Trail.longest(list);
        Station expectedStation = BER;
        Station actualStation = trail.station1();
        assertEquals(expectedStation, actualStation);
    }


    @Test
    void toStringWorksWithNonEmptyTrail(){
        List<Route> list = new ArrayList<>();
        list.add(FRILAU);
        list.add(BERFRI);
        list.add(LAUNEU);
        Trail trail = Trail.longest(list);
        String expectedString="Berne - Fribourg - Lausanne - Neuchâtel (8)";
        String actualString=trail.toString();
        assertEquals(expectedString,actualString);
    }

    @Test
    void toStringWithEmptyTrail(){
        List<Route> list = new ArrayList<>();
        Trail trail = Trail.longest(list);
        String expectedString="Empty trail";
        String actualString= trail.toString();
        assertEquals(expectedString, actualString);
    }






}

