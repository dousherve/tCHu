package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Classe publique, finale et immuable représentant un trajet.
 * 
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
public final class Trip {
    
    private final Station from, to;
    private final int points;
    
    /**
     * Retourne la liste de tous les trajets possibles
     * allant d'une des gares de la première liste (<code>from</code>)
     * à l'une des gares de la seconde liste (<code>to</code>),
     * chacun valant le nombre de points donné.
     *
     * @param from
     *          liste des gares de départ (non vide)
     * @param to
     *          liste des gares d'arrivée (non vide)
     * @param points
     *          nombre de points que valent les trajets (strictement positif)
     * @throws IllegalArgumentException
     *          si l'une des listes de gares est vide ou bien
     *          si le nombre de points n'est pas strictement positif
     * @return
     *          la liste de tous les trajets possibles allant
     *          d'une des gares de la première liste à l'une de
     *          celles de la seconde liste
     */
    public static List<Trip> all(List<Station> from, List<Station> to, int points) {
        Preconditions.checkArgument(
                ! from.isEmpty() && ! to.isEmpty() && points > 0
        );
        
        List<Trip> allTrips = new ArrayList<>();
        for (Station fromStation : from)
            for (Station toStation : to)
                allTrips.add(new Trip(fromStation, toStation, points));
        
        return Collections.unmodifiableList(allTrips);
    }
    
    /**
     * Construit un nouveau trajet entre les deux gares données
     * et valant le nombre de points donné.
     * 
     * @param from
     *          la gare de départ
     * @param to     
     *          la gare d'arrivée
     * @param points
     *          le nombre de points (strictement positif) que vaut le trajet
     * @throws IllegalArgumentException
     *          si le nombre de points n'est pas strictement positif
     * @throws NullPointerException
     *          si l'une des deux gares est <code>null</code>
     */
    public Trip(Station from, Station to, int points) {
        Preconditions.checkArgument(points > 0);
        
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.points = points;
    }
    
    /**
     * Retourne la gare de départ du trajet.
     * 
     * @return la gare de départ du trajet
     */
    public Station from() {
        return from;
    }
    
    /**
     * Retourne la gare d'arrivée du trajet.
     * 
     * @return la gare d'arrivée du trajet
     */
    public Station to() {
        return to;
    }
    
    /**
     * Retourne le nombre de points que vaut le trajet.
     * 
     * @return le nombre de points que vaut le trajet
     */
    public int points() {
        return points;
    }
    
    /**
     * Retourne le nombre de points positif que vaut le trajet
     * si les gares de départ sont connectées,
     * et le nombre de points négatif qu'il vaut sinon.
     * 
     * @param connectivity
     *          la connectivité du joueur en question
     * @return
     *          le nombre de points dont le signe dépend
     *          de la connectivité passée en paramètre
     */
    public int points(StationConnectivity connectivity) {
        return connectivity.connected(from, to) ? points : -points;
    }
    
}
