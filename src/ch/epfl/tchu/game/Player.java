package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.Map;

/**
 * Interface publique qui représente un joueur de tCHu.
 * 
 * Ses méthodes sont destinées à être appelées à différents moments de la partie,
 * soit pour communiquer certaines informations concernant son déroulement au joueur,
 * soit pour obtenir certaines informations de ce dernier, par exemple le type d'action qu'il désire effectuer.
 * 
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
public interface Player {
    
    /**
     * Type énuméré imbriqué dans <code>{@link Player}</code> qui représente
     * les trois types d'actions qu'un joueur de tCHu peut effectuer durant un tour.
     */
    enum TurnKind {
        DRAW_TICKETS, DRAW_CARDS, CLAIM_ROUTE;
    
        /**
         * Liste immuable de tous les types de tour possible,
         * dans leur ordre de déclaration.
         */
        public static final List<TurnKind> ALL = List.of(values());
    }
    
    /**
     * Méthode appelée au début de la partie qui communique au joueur
     * sa propre identité <code>ownId</code>, ainsi que les noms des différents joueurs,
     * le sien inclus, qui se trouvent dans <code>playerNames</code>.
     * 
     * @param ownId
     *          l'identité du joueur représenté par le récepteur
     * @param playerNames
     *          les noms des différents joueurs de la partie
     */
    void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames);
    
    /**
     * Méthode appelée chaque fois qu'une information doit être communiquée au joueur au cours de la partie.
     * Cette information est donnée sous la forme de la chaîne de caractères <code>info</code>.
     * 
     * @param info
     *          l'information à communiquer au joueur au cours de la partie
     */
    void receiveInfo(String info);
    
    /**
     * Méthode appelée chaque fois que l'état du jeu a changé, pour informer le joueur
     * de la composante publique de ce nouvel état, <code>newState</code>,
     * ainsi que de son propre état, <code>ownState</code>.
     * 
     * @param newState
     *          la composante publique du nouvel état de la partie
     * @param ownState
     *          le nouvel état du joueur
     */
    void updateState(PublicGameState newState, PlayerState ownState);
    
    /**
     * Méthode appelée au début de la partie pour communiquer au joueur
     * les cinq billets qui lui ont été distribués.
     * 
     * @param tickets
     *          les billets distribués au joueur
     */
    void setInitialTicketChoice(SortedBag<Ticket> tickets);
    
    /**
     * Méthode appelée au début de la partie pour demander au joueur
     * quels billets il désire garder parmi ceux qu'on lui a distribué initialement.
     * 
     * @return les billets que le joueur choisit de garder
     */
    SortedBag<Ticket> chooseInitialTickets();
    
    /**
     * Méthode appelée au début du tour d'un joueur pour savoir
     * quel type d'action il désire effectuer durant ce tour.
     * 
     * @return l'action que le joueur désire effectuer durant ce tour
     */
    TurnKind nextTurn();
    
    /**
     * Méthode appelée lorsque le joueur a décidé de tirer des billets supplémentaires en cours de partie,
     * afin de lui communiquer les billets tirés et de savoir lesquels il garde.
     * 
     * @param options
     *          les billets possibles que le joueur peut choisir de garder
     * @return
     *          les billets que le joueur désire garder
     */
    SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options);
    
    /**
     * Méthode appelée lorsque le joueur a décidé de tirer des cartes wagon/locomotive,
     * afin de savoir d'où il désire les tirer : d'un des emplacements contenant 
     * une carte face visible — auquel cas la valeur retournée est comprise entre 0 et 4 inclus —,
     * ou de la pioche — auquel cas la valeur retournée vaut <code>Constants.DECK_SLOT</code> (-1).
     * 
     * @return l'emplacement duquel le joueur veut tirer les cartes
     */
    int drawSlot();
    
    /**
     * Méthode appelée lorsque le joueur a décidé de (tenter de) s'emparer d'une route,
     * afin de savoir de quelle route il s'agit.
     * 
     * @return la route dont le joueur veut s'emparer
     */
    Route claimedRoute();
    
    /**
     * Méthode appelée lorsque le joueur a décidé de (tenter de) s'emparer d'une route,
     * afin de savoir quelle(s) carte(s) il désire initialement utiliser pour cela.
     * 
     * @return les cartes utilisées initialement par le joueur pour s'emparer de la route
     */
    SortedBag<Card> initialClaimCards();
    
    /**
     * Méthode appelée lorsque le joueur a décidé de tenter de s'emparer d'un tunnel
     * et que des cartes additionnelles sont nécessaires afin de savoir quelle(s) carte(s) il désire utiliser pour cela,
     * les possibilités lui étant passées en argument.
     * Si le multiensemble retourné est vide, cela signifie que le joueur ne désire pas (ou ne peut pas)
     * choisir l'une de ces possibilités.
     * 
     * @param options
     *          la liste des ensembles de cartes additionelles possibles pour s'emparer du tunnel
     *          convoité par le joueur
     * @return
     *          l'ensemble de cartes additionnelles qu'il a choisi,
     *          qui est vide s'il ne désire pas (ou ne peut pas) s'emparer du tunnel.
     */
    SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);

}
