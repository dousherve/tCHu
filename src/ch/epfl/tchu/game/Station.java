package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * Classe publique, finale et immuable représentant une gare.
 * 
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
public final class Station {
    
    private final int id;
    private final String name;
    
    /**
     * Construit une gare ayant le numéro d'identification et le nom donnés.
     * 
     * @param id
     *          l'identifiant unique de la gare
     * @param name
     *          le nom de la gare
     * @throws IllegalArgumentException
     *          si le numéro d'identification est strictement négatif
     */
    public Station(int id, String name) {
        Preconditions.checkArgument(id >= 0);
        
        this.id = id;
        this.name = name;
    }
    
    /**
     * Retourne le numéro d'identification de la gare.
     * 
     * @return le numéro d'identification de la gare
     */
    public int id() {
        return id;
    }
    
    /**
     * Retourne le nom de la gare.
     * 
     * @return le nom de la gare
     */
    public String name() {
        return name;
    }
    
    /**
     * Retourne le nom de la gare.
     *
     * @return le nom de la gare
     */
    @Override
    public String toString() {
        return name;
    }
    
}
