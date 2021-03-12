package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.Collections;
import java.util.List;

public class PublicPlayerState {
    
    private final int ticketCount, cardCount;
    private final List<Route> routes;
    private final int carCount, claimPoints;
    
    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes) {
        Preconditions.checkArgument(ticketCount >= 0 && cardCount >= 0);
        
        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        this.routes = Collections.unmodifiableList(routes);
        
        this.carCount = Constants.INITIAL_CAR_COUNT - routes.stream().mapToInt(Route::length).sum();
        this.claimPoints = routes.stream().mapToInt(Route::claimPoints).sum();
    }
    
    public int ticketCount() {
        return ticketCount;
    }
    
    public int cardCount() {
        return cardCount;
    }
    
    public List<Route> routes() {
        return routes;
    }
    
    public int carCount() {
        return carCount;
    }
    
    public int claimPoints() {
        return claimPoints;
    }
    
}
