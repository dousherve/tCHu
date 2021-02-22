package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Trip {
    
    private final Station from, to; // TODO: Demander si majuscule aux attributs finaux
    private final int points;
    
    public Trip(Station from, Station to, int points) {
        if (! (points > 0))
            throw new IllegalArgumentException();
        
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.points = points;
    }
    
    public static List<Trip> all(List<Station> from, List<Station> to, int points) {
        if (from.isEmpty() || to.isEmpty() || ! (points > 0))
            throw new IllegalArgumentException();
        
        List<Trip> allTrips = new ArrayList<>();
    
        for (Station f : from) {
            for (Station t : to) {
                allTrips.add(new Trip(f, t, points));
            }
        }
        
        return allTrips;
    }
    
    public Station from() {
        return from;
    }
    
    public Station to() {
        return to;
    }
    
    public int points() {
        return points;
    }
    
    public int points(StationConnectivity connectivity) {
        return (connectivity.connected(from, to)) ? points() : -points(); // TODO: Demander à un assistant pour points()
    }
}
