package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
        
        final TreeSet<String> COUNTRY_NAMES = (TreeSet<String>) Set.of("Allemagne", "Autriche", "France", "Italie");
        
        if (COUNTRY_NAMES.contains(FROM_STATION_NAME)) {
            // Billet pays à pays,
            // car le nom de la station de départ est un pays
            
            // TODO: implémenter
        } else {
            // Billet ville à pays
            
            
        }
        
        // TODO: retourner la bonne valeur
        return "";
    }
    
    public String text() {
        return text;
    }
    
    public int points(StationConnectivity connectivity) {
        // TODO: implement
        
        return 0; 
    }
    
    @Override
    public int compareTo(Ticket that) {
        // TODO: implement
        return 0;
    }
    
}
