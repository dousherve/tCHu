package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Classe immuable représentant un billet.
 * 
 * @author Louis Hervé (312937)
 * @author Mallory Henriet (311258)
 */
public final class Ticket implements Comparable<Ticket> {
    
    private final List<Trip> trips;
    private final String text;
    
    /**
     * Construit un billet constitué de la liste de trajets donnée.
     * 
     * @param trips
     *          liste non vide de trajets dont toutes les gares de départ ont le même nom
     * @throws IllegalArgumentException
     *          si la liste de trajets est vide ou bien si toutes les gares de départ
     *          des trajets contenus dans la liste n'ont pas le même nom
     */
    public Ticket(List<Trip> trips) {
        Preconditions.checkArgument(! trips.isEmpty());
        
        final String FIRST_STATION_NAME = trips
                .get(0)
                .from()
                .name();

        for (int i = 1; i < trips.size(); ++i) {
            final String CURRENT_STATION_NAME = trips
                    .get(i)
                    .from()
                    .name();

            Preconditions.checkArgument(
                    CURRENT_STATION_NAME.equals(FIRST_STATION_NAME)
            );
        }
        
        this.trips = trips;
        this.text = computeText(trips);
    }
    
    /**
     * Construit un billet constitué d'un unique trajet décrit par les paramètres donnés.
     * 
     * @param from
     *          la gare de départ du trajet
     * @param to
     *          la gare d'arrivée du trajet
     * @param points
     *          le nombre de points que vaut le trajet
     */
    public Ticket(Station from, Station to, int points) {
        this(Collections.singletonList(
                new Trip(from, to, points)
        ));
    }
    
    /**
     * Méthode privée et statique qui retourne la représentation textuelle du billet
     * selon la liste de trajets passée en paramètre.
     * 
     * @param trips
     *          la liste de trajets du billet
     * @return
     *          la représentation textuelle du billet
     */
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
        
        final String FORMATTED_DESCRITPIONS = String.join(
                ", ", destinationsDescriptions
        );
    
        return String.format(
                "%s - {%s}",
                FROM_STATION_NAME,
                FORMATTED_DESCRITPIONS
        );
    }
    
    /**
     * Retourne la représentation textuelle du billet.
     * 
     * @return
     *          la représentation textuelle du billet
     */
    public String text() {
        return text;
    }
    
    /**
     * Retourne le nombre de points que vaut le billet,
     * en prenant en compte la connectivité donnée.
     * 
     * @param connectivity
     *          la connectivité du joueur qui possède le billet
     * @return
     *          le nombre de points que vaut le billet en prenant en compte
     *          la connectivité du joueur qui le possède
     */
    public int points(StationConnectivity connectivity) {
        final int FIRST_TRIP_POINTS = trips
                .get(0)
                .points(connectivity);
        
        if (trips.size() == 1) {
            // Billet ville à ville : trajet unique
            return FIRST_TRIP_POINTS;
        }
    
        /* 
           Billet ville à pays ou bien pays à pays :
           On cherche le score maximum parmi tous les trajets.
           Ainsi, le comportement min/max imposé sera automatiquement
           pris en compte grâce au signe des valeurs retournées par le trajet dont il est question.
        */
        int maxScore = FIRST_TRIP_POINTS;
        for (int i = 1; i < trips.size(); ++i) {
            maxScore = Math.max(
                    maxScore,
                    trips.get(i).points(connectivity)
            );
        }
            
        return maxScore;
    }

    /**
     * Compare ce billet à celui passé en argument
     * selon l'ordre alphabétique de leur représentation textuelle.
     * 
     * @param that
     *          le billet avec lequel on effectue la comparaison
     * @return un entier strictement négatif si this est strictement plus petit que that,
     *         un entier strictement positif si this est strictement plus grand que that,
     *         et zéro si les deux sont égaux
     */
    @Override
    public int compareTo(Ticket that) {
        return this.text.compareTo(that.text);
    }

    /**
     * Retourne la même valeur que la méthode text() : 
     *
     * @return
     *          la représentation textuelle
     */
    @Override
    public String toString() {
        return text;
    }
    
}
