package ch.epfl.tchu.game;

/**
 * Interface représentant la connectivité du réseau d'un joueur,
 * c'est-à-dire le fait que deux gares du réseau de tCHu
 * soient reliées ou non par ce réseau.
 * 
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
public interface StationConnectivity {
    
    /**
     * Retourne vrai si et seulement si les gares données
     * sont reliées par le réseau du joueur.
     * 
     * @param s1
     *          la gare de départ
     * @param s2
     *          la gare d'arrivée
     * @return
     *          un booléen indiquant si les gares passées en paramètre
     *          sont reliées par le réseau du joueur
     */
    boolean connected(Station s1, Station s2);
    
}
