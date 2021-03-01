package ch.epfl.tchu;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.util.ArrayList;
import java.util.List;

public class ManualTests {
    
    public static void main(String[] args) {
        final int INDEX = 132 - 91;
        final Route r = ChMap.routes().get(INDEX);
    
        System.out.println(r.possibleClaimCards());
        System.out.println(r.possibleClaimCards().size());
    
        System.out.println("Returned : " + r.additionalClaimCardsCount(
                r.possibleClaimCards().get(15),
                SortedBag.of(List.of(Card.LOCOMOTIVE, Card.WHITE, Card.BLACK))
        ));
        
        List<Route> routes = new ArrayList<>();
        routes.add(ChMap.routes().get(2));
        routes.add(ChMap.routes().get(6));
        
        Trail trail = Trail.longest(routes);
        System.out.println(trail.toString());
    }
    
}
