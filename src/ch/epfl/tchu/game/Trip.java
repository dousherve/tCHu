package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Trip {
    
    private final Station from, to; // TODO: Demander si majuscule aux attributs finaux
    private final int points;
    
    public Trip(Station from, Station to, int points) {
        Preconditions.checkArgument(points > 0);
        
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.points = points;
    }
    
    public static List<Trip> all(List<Station> from, List<Station> to, int points) {
        Preconditions.checkArgument(! from.isEmpty());
        Preconditions.checkArgument(! to.isEmpty());
        Preconditions.checkArgument(points > 0);
        
        List<Trip> allTrips = new ArrayList<>();
        
        for (Station fromStation : from) {
            for (Station toStation : to) {
                allTrips.add(
                        new Trip(fromStation, toStation, points)
                );
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
        // TODO: points() ou points ?
        return (connectivity.connected(from, to)) ? points : -points;
    }
    
}
