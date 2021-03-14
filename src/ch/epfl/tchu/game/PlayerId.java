package ch.epfl.tchu.game;

import java.util.List;

/**
 * Type énuméré qui représente l'identité d'un joueur.
 * Comme tChu ne possède que 2 joueurs alors son nombre d'élément est de 2.
 *
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
public enum PlayerId {
    
    PLAYER_1, PLAYER_2;

    public static final List<PlayerId> ALL = List.of(values());
    public static final int COUNT = ALL.size();

    /**
     * Retourne l'identité du joueur qui suit celui auquel on applique la méthode.
     * C'est-à-dire : pour PLAYER_1 on retourne PLAYER_2 et inversement.
     *
     * @return
     *          l'identité du joueur qui suit
     */
    public PlayerId next() {
        // TODO: equals() ou this == ?
        return (this == PLAYER_1) ? PLAYER_2 : PLAYER_1;
    }
    
}
