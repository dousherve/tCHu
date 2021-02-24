package ch.epfl.tchu.game;

/**
 * Interface représentant la connectivité du réseau d'un joueur.
 * 
 * @author Mallory Henriet (311258)
 */
public interface StationConnectivity {
    
    /**
     * Retourne vrai si et seulement si les gares passées en paramètre
     * sont reliées par le réseau du joueur.
     * 
     * @param s1
     *          la gare de départ
     * @param s2
     *          la gare d'arrivée
     * @return un booléen indiquant si les gares passées en paramètre
     *          sont reliées par le réseau du joueur
     */
    boolean connected(Station s1, Station s2);
    
}
