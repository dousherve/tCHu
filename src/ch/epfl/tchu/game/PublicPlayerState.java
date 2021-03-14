package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.Collections;
import java.util.List;

/**
 * Classe publique et immuable qui représente la partie publique de l'état d'un joueur.
 *
 * C'est-à-dire : son nombre de billets, son nombre de cartes wagons en possession,
 * la liste des routes dont il s'est emparé et le nombre de point de construction qu'il a ainsi obtenu.
 *
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
public class PublicPlayerState {
    
    private final int ticketCount, cardCount;
    private final List<Route> routes;
    private final int carCount, claimPoints;

    /**
     * Construit l'état public d'un joueur possédant le nombre de billets et de cartes donnés
     * ainsi que la liste de routes donnée.
     *
     * @param ticketCount
     *          le nombre de billets
     * @param cardCount
     *          le nombre de cartes
     * @param routes
     *          la liste des routes dont le joueur s'est emparé
     * @throws IllegalArgumentException
     *          si <code>ticketCount</code> ou si <code>cardCount</code> est stricement négatif (<0)
     */
    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes) {
        Preconditions.checkArgument(ticketCount >= 0 && cardCount >= 0);
        
        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        this.routes = Collections.unmodifiableList(routes);
        
        this.carCount = Constants.INITIAL_CAR_COUNT - routes.stream().mapToInt(Route::length).sum();
        this.claimPoints = routes.stream().mapToInt(Route::claimPoints).sum();
    }

    /**
     * Retourne le nombre de billets que possède le joueur.
     *
     * @return
     *          le nombre de billets que possède le joueur
     */
    public int ticketCount() {
        return ticketCount;
    }

    /**
     * Retourne le nombre de cartes que possède le joueur.
     *
     * @return
     *          le nombre cartes que possède le joueur
     */
    public int cardCount() {
        return cardCount;
    }

    /**
     * Retourne les routes dont le joueur s'est emparé.
     *
     * @return
     *          les routes dont le joueur s'est emparé
     */
    public List<Route> routes() {
        return routes;
    }

    /**
     * Retourne le nombre de wagons que possède le joueur.
     *
     * @return
     *          le nombre de wagons que possède le joueur
     */
    public int carCount() {
        return carCount;
    }

    /**
     * Retourne le nombre de points de construction obtenus par le joueur.
     *
     * @return
     *          le nombre de points de construction obtenus par le joueur
     */
    public int claimPoints() {
        return claimPoints;
    }
    
}
