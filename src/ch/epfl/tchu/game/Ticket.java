package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Classe publique, finale et immuable représentant un billet.
 * 
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
public final class Ticket implements Comparable<Ticket> {
    
    private final List<Trip> trips;
    private final String text;
    
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
        final Trip firstTrip = trips.get(0);
        final String fromStationName = firstTrip.from().name();
        
        if (trips.size() == 1) {
            // Billet ville à ville, car il contient un seul trajet
            final String toStationName = firstTrip.to().name();
            
            return String.format(
                    "%s - %s (%d)",
                    fromStationName,
                    toStationName,
                    firstTrip.points()
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
                fromStationName,
                String.join(", ", destinationsDescriptions)
        );
    }
    
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
        
        final String firstStationName = trips.get(0).from().name();
        for (Trip trip : trips) {
            Preconditions.checkArgument(
                    trip.from().name().equals(firstStationName)
            );
        }
        
        this.trips = List.copyOf(trips);
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
        this(List.of(
                new Trip(from, to, points)
        ));
    }
    
    /**
     * Retourne la représentation textuelle du billet.
     * 
     * @return la représentation textuelle du billet
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
        final int firstTripPoints = trips.get(0).points(connectivity);
        
        // Billet ville à ville : trajet unique
        if (trips.size() == 1)
            return firstTripPoints;
    
        /* 
           Billet ville à pays ou bien pays à pays :
           On cherche le score maximum parmi tous les trajets.
           Ainsi, le comportement min/max imposé sera automatiquement
           pris en compte grâce au signe des valeurs retournées par le trajet en question.
        */
        int maxScore = firstTripPoints;
        for (Trip trip : trips) {
            maxScore = Math.max(
                    maxScore,
                    trip.points(connectivity)
            );
        }
            
        return maxScore;
    }

    /**
     * Compare le billet auquel on l'applique à celui passé en argument (<code>that</code>)
     * par ordre alphabétique de leur représentation textuelle,
     * et retourne un entier strictement négatif si il est strictement plus petit que <code>that</code>,
     * un entier strictement positif si il est strictement plus grand que <code>that</code>,
     * et zéro si les deux sont égaux.
     * 
     * @param that
     *          le billet avec lequel on effectue la comparaison
     * @return 
     *         un entier strictement négatif si <code>this</code> est strictement plus petit que <code>that</code>,
     *         un entier strictement positif si <code>this</code> est strictement plus grand que <code>that</code>,
     *         et zéro si les deux sont égaux
     */
    @Override
    public int compareTo(Ticket that) {
        return this.text.compareTo(that.text);
    }

    /**
     * Retourne la même valeur que la méthode <code>text()</code> :
     * la représentation textuelle du billet.
     *
     * @return la représentation textuelle du billet
     */
    @Override
    public String toString() {
        return text;
    }
    
}
