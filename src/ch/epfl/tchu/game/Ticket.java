package ch.epfl.tchu.game;

import java.util.Collections;
import java.util.List;

public final class Ticket implements Comparable<Ticket> {
    
    private final List<Trip> trips;
    private final String text;
    
    public Ticket(List<Trip> trips) {
        if (trips.isEmpty())
            throw new IllegalArgumentException();
        
        final String FIRST_NAME = trips.get(0).from().name();
        for (int i = 1; i < trips.size(); ++i) {
            if (! trips.get(i).from().name().equals(FIRST_NAME))
                throw new IllegalArgumentException();
        }
        
        this.trips = trips;
        this.text = computeText(trips);
    }
    
    public Ticket(Station from, Station to, int points) {
        this(Collections.singletonList(new Trip(from, to, points)));
    }
    
    private static String computeText(List<Trip> trips) {
        final Trip FIRST_TRIP = trips.get(0);
        final Station FROM = FIRST_TRIP.from();
        
        if (trips.size() == 1) {
            // Billet ville-à-ville
            final Station TO = FIRST_TRIP.to();
            return String.format("%s - %s (%d)", FROM.name(), TO.name(), FIRST_TRIP.points());
        }
        
        final List<String> COUNTRIES = List.of("Allemagne", "Autriche", "France", "Italie");
        if (COUNTRIES.contains(FROM.name())) {
            // Billet pays-à-pays
            
        }
        
        // TODO: retourner la bonne valeur
        return "";
    }
    
    public String text() {
        return text;
    }
    
    @Override
    public int compareTo(Ticket o) {
        return 0;
    }
}
