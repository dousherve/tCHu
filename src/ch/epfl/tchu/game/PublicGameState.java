package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Classe immuable qui représente la composante publique de l'état d'une partie de tCHu.
 *
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
public class PublicGameState {
    
    private final int ticketsCount;
    private final PublicCardState cardState;
    private final PlayerId currentPlayerId, lastPlayer;
    private final Map<PlayerId, PublicPlayerState> playerState;
    
    /**
     * Construit la partie publique de l'état d'une partie de tCHu dans laquelle
     * la pioche de billets a une taille de <code>ticketsCount</code>,
     * l'état public des cartes wagon/locomotive est <code>cardState</code>,
     * le joueur courant est <code>currentPlayerId</code>,
     * l'état public des joueurs est contenu dans <code>playerState</code>,
     * et l'identité du dernier joueur est <code>lastPlayer</code> (qui peut être <code>null</code>)
     * 
     * @param ticketsCount
     *          la taille de la pioche de billets
     * @param cardState
     *          l'état public des cartes wagon/locomotive
     * @param currentPlayerId
     *          l'identité du joueur courant
     * @param playerState
     *          l'état public des joueurs
     * @param lastPlayer
     *          l'identité du dernier joueur (peut être <code>null</code>)
     * @throws IllegalArgumentException
     *          si la taille de la pioche est strictement négative, ou
     *          si <code>playerState</code> ne contient pas exactement deux paires clé/valeur
     * @throws NullPointerException
     *          si l'un des autres arguments (<code>lastPlayer</code> excepté) est <code>null</code>
     */
    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId, Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer) {
        Preconditions.checkArgument(ticketsCount >= 0 && playerState.size() == 2);
        
        this.ticketsCount = ticketsCount;
        this.cardState = Objects.requireNonNull(cardState);
        this.currentPlayerId = Objects.requireNonNull(currentPlayerId);
        this.playerState = Map.copyOf(Objects.requireNonNull(playerState));
        this.lastPlayer = lastPlayer;
    }
    
    /**
     * Retourne la taille de la pioche de billets.
     * 
     * @return la taille de la pioche de billets
     */
    public int ticketsCount() {
        return ticketsCount;
    }
    
    /**
     * Retourne vrai si et seulement si il est possible de tirer des billets,
     * c'est-à-dire si la pioche n'est pas vide.
     * 
     * @return vrai si et seulement si il est possible de tirer des billets
     */
    public boolean canDrawTickets() {
        return ticketsCount > 0;
    }
    
    /**
     * Retourne la partie publique de l'état des cartes wagon/locomotive.
     * 
     * @return la partie publique de l'état des cartes wagon/locomotive
     */
    public PublicCardState cardState() {
        return cardState;
    }
    
    /**
     * Retourne vrai si et seulement si il est possible de tirer des cartes,
     * c'est-à-dire si la pioche et la défausse contiennent entre elles au moins 5 cartes.
     * 
     * @return vrai si et seulement si il est possible de tirer des cartes
     */
    public boolean canDrawCards() {
        return (cardState.deckSize() + cardState.discardsSize()) >= 5;
    }
    
    /**
     * Retourne l'identité du joueur actuel.
     * 
     * @return  l'identité du joueur actuel
     */
    public PlayerId currentPlayerId() {
        return currentPlayerId;
    }
    
    /**
     * Retourne la partie publique de l'état du joueur d'identité donnée.
     * 
     * @param playerId
     *          le joueur dont on veut connaître la partie publique de l'état
     * @return
     *          la partie publique de l'état du joueur d'identité donnée
     */
    public PublicPlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }
    
    /**
     * Retourne la partie publique de l'état du joueur courant.
     * 
     * @return la partie publique de l'état du joueur courant
     */
    public PublicPlayerState currentPlayerState() {
        return playerState(currentPlayerId);
    }
    
    /**
     * Retourne la totalité des routes dont l'un ou l'autre des joueurs s'est emparé.
     * 
     * @return la totalité des routes dont l'un ou l'autre des joueurs s'est emparé
     */
    public List<Route> claimedRoutes() {
        return playerState.values().stream()
                .flatMap(state -> state.routes().stream())
                .collect(Collectors.toUnmodifiableList());
    }
    
    /**
     * Retourne l'identité du dernier joueur, ou <code>null</code>
     * si elle n'est pas encore connue car le dernier tour n'a pas commencé.
     * 
     * @return l'identité du dernier joueur (ou <code>null</code>)
     */
    public PlayerId lastPlayer() {
        return lastPlayer;
    }
    
}
