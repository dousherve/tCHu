package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * Classe immuable représentant une gare.
 * 
 * @author Mallory Henriet (311258)
 */
public final class Station {
    
    private final int id;
    private final String name;
    
    /**
     * Construit une nouvelle gare, munie d'un identifiant unique et d'un nom.
     * 
     * @param id
     *          l'identifiant unique de la gare
     * @param name
     *          le nom de la gare
     * @throws IllegalArgumentException
     *          si le nombre de points est négatif ou nul
     */
    public Station(int id, String name) {
        Preconditions.checkArgument(! (id < 0));
        
        this.id = id;
        this.name = name;
    }
    
    /**
     * Retourne l'identifiant unique de cette gare.
     * 
     * @return l'identifiant unique de cette gare
     */
    public int id() {
        return id;
    }
    
    /**
     * Retourne le nom de cette gare.
     * 
     * @return le nom de cette gare
     */
    public String name() {
        return name;
    }
    
    /**
     * Retourne le nom de cette gare.
     *
     * @return le nom de cette gare
     */
    @Override
    public String toString() {
        return name;
    }
    
}
