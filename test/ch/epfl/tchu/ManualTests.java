package ch.epfl.tchu;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.util.ArrayList;
import java.util.List;

public class ManualTests {
    
    private static int toIndex(int lineNumber) {
        return lineNumber - 92;
    }
    
    public static void main(String[] args) {
        final int INDEX = toIndex(133);
        final Route r = ChMap.routes().get(INDEX);
    
        System.out.println("Possible claim cards : " + r.possibleClaimCards());
        System.out.println("Possible claim cards size : " + r.possibleClaimCards().size());
    
        System.out.println("Returned : " + r.additionalClaimCardsCount(
                r.possibleClaimCards().get(15),
                SortedBag.of(List.of(Card.LOCOMOTIVE, Card.WHITE, Card.BLACK))
        ));
        
        List<Route> routes = new ArrayList<>();
        routes.add(ChMap.routes().get(toIndex(158)));
        routes.add(ChMap.routes().get(toIndex(157)));
        routes.add(ChMap.routes().get(toIndex(110)));
        routes.add(ChMap.routes().get(toIndex(105)));
        routes.add(ChMap.routes().get(toIndex(111)));
        routes.add(ChMap.routes().get(toIndex(108)));
        
        Trail trail = Trail.longest(routes);
        System.out.println("Longest trail : " + trail.toString());
    }
    
}
