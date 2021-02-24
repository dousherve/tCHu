package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

// TODO: documenter le code !

public final class Ticket implements Comparable<Ticket> {
    
    private final List<Trip> trips;
    private final String text;
    
    public Ticket(List<Trip> trips) {
        Preconditions.checkArgument(! trips.isEmpty());
        
        final String FIRST_STATION_NAME = trips.get(0).from().name();
        for (int i = 1; i < trips.size(); ++i) {
            final String CURRENT_STATION_NAME = trips.get(i).from().name();
            Preconditions.checkArgument(
                    CURRENT_STATION_NAME.equals(FIRST_STATION_NAME)
            );
        }
        
        this.trips = trips;
        this.text = computeText(trips);
    }
    
    public Ticket(Station from, Station to, int points) {
        this(Collections.singletonList(
                new Trip(from, to, points))
        );
    }
    
    private static String computeText(List<Trip> trips) {
        final Trip FIRST_TRIP = trips.get(0);
        final String FROM_STATION_NAME = FIRST_TRIP.from().name();
        
        if (trips.size() == 1) {
            // Billet ville à ville, car il contient un seul trajet
            final String TO_STATION_NAME = FIRST_TRIP.to().name();
            
            return String.format(
                    "%s - %s (%d)",
                    FROM_STATION_NAME,
                    TO_STATION_NAME,
                    FIRST_TRIP.points()
            );
        }
        
        // Billet ville à pays ou bien pays à pays
    
        Set<String> destinationsDescriptions = new TreeSet<>();
        
        for (Trip trip : trips) {
            destinationsDescriptions.add(
                    String.format(
                            "%s (%d)",
                            trip.to().name(),
                            trip.points()
                    )
            );
        }
    
        return String.format(
                "%s - {%s}",
                FROM_STATION_NAME,
                String.join(", ", destinationsDescriptions)
        );
    }
    
    public String text() {
        return text;
    }
    
    public int points(StationConnectivity connectivity) {
        final int FIRST_TRIP_POINTS = trips.get(0).points(connectivity);
        
        if (trips.size() == 1) {
            // Billet ville à ville : trajet unique
            return FIRST_TRIP_POINTS;
        }
    
        // Billet ville à pays ou bien pays à pays :
        // On cherche le score maximum parmi tous les trajets.
        // Ainsi, le comportement min/max imposé sera automatiquement
        // pris en compte grâce au signe des valeurs retournées.
        
        int maxScore = FIRST_TRIP_POINTS;
        for (int i = 1; i < trips.size(); ++i) {
            maxScore = Math.max(
                    maxScore,
                    trips.get(i).points(connectivity)
            );
        }
            
        return maxScore;
    }
    
    @Override
    public int compareTo(Ticket that) {
        return this.text.compareTo(that.text);
    }
    
}
