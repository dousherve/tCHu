package ch.epfl.tchu.game;

import java.util.List;

/**
 * Type énuméré qui représente l'identité de l'un des deux joueurs de tCHu.
 *
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
public enum PlayerId {
    
    PLAYER_1, PLAYER_2;
    
    /**
     * Liste immuable contenant les deux joueurs du type énuméré,
     * dans leur ordre de définition.
     */
    public static final List<PlayerId> ALL = List.of(values());
    /**
     * Nombre total de joueurs.
     */
    public static final int COUNT = ALL.size();

    /**
     * Retourne l'identité du joueur qui suit celui auquel on applique la méthode.
     * C'est-à-dire : pour <code>PLAYER_1</code> on retourne <code>PLAYER_2</code> et inversement.
     *
     * @return l'identité du joueur qui suit
     */
    public PlayerId next() {
        return (this == PLAYER_1) ? PLAYER_2 : PLAYER_1;
    }
    
}
