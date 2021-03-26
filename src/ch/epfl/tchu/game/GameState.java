package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

/**
 * Classe publique, finale et immuable qui représente la composante privée l'état d'une partie de tCHu.
 * Elle hérite de <code>{@link PublicGameState}</code>, qui représente sa composante publique.
 *
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
public final class GameState extends PublicGameState {
    
    private final Deck<Ticket> tickets;
    private final CardState cardState;
    private final Map<PlayerId, PlayerState> playerState;
    
    /**
     * Retourne l'état initial d'une partie de tCHu dans laquelle la pioche des billets
     * contient les billets donnés et la pioche des cartes contient les cartes de <code>Constants.ALL_CARDS</code>,
     * sans les 8 (2×4) du dessus, distribuées aux joueurs.
     * Ces pioches sont mélangées au moyen du générateur aléatoire donné,
     * qui est aussi utilisé pour choisir au hasard l'identité du premier joueur.
     * 
     * @param tickets
     *          les billets à ajouter à la pioche
     * @param rng
     *          le générateur aléatoire donné
     * @return
     *          l'état initial d'une partie de tCHu
     */
    public static GameState initial(SortedBag<Ticket> tickets, Random rng) {
        final Deck<Card> allCards = Deck.of(Constants.ALL_CARDS, rng); 
        final Deck<Card> deckCards = allCards.withoutTopCards(8);
        
        final PlayerId firstPlayerId = PlayerId.ALL.get(rng.nextInt(PlayerId.COUNT));
        final PlayerId secondPlayerId = firstPlayerId.next();
        
        final PlayerState firstPlayerState = PlayerState.initial(allCards.topCards(4));
        final PlayerState secondPlayerState = PlayerState.initial(allCards.withoutTopCards(4).topCards(4));
        
        final Map<PlayerId, PlayerState> playerState = new EnumMap<>(PlayerId.class);
        playerState.put(firstPlayerId, firstPlayerState);
        playerState.put(secondPlayerId, secondPlayerState);
        
        return new GameState(
                Deck.of(tickets, rng),
                CardState.of(deckCards),
                firstPlayerId,
                playerState,
                null
        );
    }
    
    private GameState(Deck<Ticket> tickets, CardState cardState, PlayerId currentPlayerId, Map<PlayerId, PlayerState> playerState, PlayerId lastPlayer) {
        super(tickets.size(), cardState, currentPlayerId, Map.copyOf(playerState), lastPlayer);
        
        this.tickets = tickets;
        this.cardState = cardState;
        this.playerState = Map.copyOf(playerState);
    }
    
    /**
     * Retourne l'état complet du joueur d'identité donnée, et pas seulement sa partie publique.
     * 
     * @param playerId
     *          le joueur dont on veut connaître la partie publique de l'état
     * @return
     *          l'état complet du joueur d'identité donnée
     */
    @Override
    public PlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }
    
    /**
     * Retourne l'état complet du joueur courant, et pas seulement sa partie publique.
     * 
     * @return
     *          l'état complet du joueur courant
     */
    @Override
    public PlayerState currentPlayerState() {
        return playerState(currentPlayerId());
    }
    
    // MARK:- Billets et cartes
    
    /**
     * Retourne les <code>count</code> billets du sommet de la pioche.
     * 
     * @param count
     *          le nombre de billets à retourner
     * @throws IllegalArgumentException
     *          si <code>count</code> n'est pas compris entre 0 et la taille de la pioche (inclus)
     * @return
     *          les <code>count</code> billets du sommet de la pioche
     */
    public SortedBag<Ticket> topTickets(int count) {
        return tickets.topCards(count);
    }
    
    /**
     * Retourne un état identique au récepteur,
     * mais sans les <code>count</code> billets du sommet de la pioche.
     * 
     * @param count
     *          le nombre de billets à retirer du sommet de la nouvelle pioche
     * @throws IllegalArgumentException
     *          si <code>count</code> n'est pas compris entre 0 et la taille de la pioche (inclus)
     * @return
     *          un état identique sans un certain nombre de billets du sommet de la pioche
     */
    public GameState withoutTopTickets(int count) {
        return new GameState(
                tickets.withoutTopCards(count),
                cardState,
                currentPlayerId(),
                playerState, lastPlayer()
        );
    }
    
    /**
     * Retourne la carte au sommet de la pioche.
     * 
     * @throws IllegalArgumentException
     *          si la pioche est vide
     * @return
     *          la carte au sommet de la pioche
     */
    public Card topCard() {
        return cardState.topDeckCard();
    }
    
    /**
     * Retourne un état identique au récepteur mais sans la carte au sommet de la pioche.
     * 
     * @throws IllegalArgumentException
     *          si la pioche est vide
     * @return
     *          un état identique au récepteur mais sans la carte au sommet de la pioche
     */
    public GameState withoutTopCard() {
        return new GameState(
                tickets,
                cardState.withoutTopDeckCard(),
                currentPlayerId(),
                playerState,
                lastPlayer()
        );
    }
    
    /**
     * Retourne un état identique au récepteur mais avec les cartes données ajoutées à la défausse.
     * 
     * @param discardedCards
     *          les cartes à ajouter à la défausse
     * @return
     *          un état identique avec les cartes données ajoutées à la défausse
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) {
        return new GameState(
                tickets,
                cardState.withMoreDiscardedCards(discardedCards),
                currentPlayerId(),
                playerState,
                lastPlayer()
        );
    }
    
    /**
     * Retourne un état identique au récepteur sauf si la pioche de cartes est vide,
     * auquel cas elle est recréée à partir de la défausse,
     * mélangée au moyen du générateur aléatoire donné.
     * 
     * @param rng
     *          le générateur aléatoire à utiliser pour mélanger les cartes
     * @return
     *          un état identique sauf si la pioche de cartes est vide,
     *          elle est recréée et mélangée à partir de la défausse
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng) {
        return cardState.isDeckEmpty()
                ? new GameState(
                        tickets,
                        cardState.withDeckRecreatedFromDiscards(rng),
                        currentPlayerId(),
                        playerState,
                        lastPlayer()
                ) : this;
    }
    
    // MARK:- États dérivés de l'état courant
    
    /**
     * Retourne un état identique au récepteur mais dans lequel
     * les billets donnés ont été ajoutés à la main du joueur donné.
     * 
     * @param playerId
     *          l'identité du joueur auquel on ajoute les billets donnés
     * @param chosenTickets
     *          les billets à ajouter à la main du joueur donné
     * @throws IllegalArgumentException
     *          si le joueur en question possède déjà au moins un billet
     * @return
     *          un état identique au récepteur mais qui contient
     *          les nouveaux billets du joueur donné
     */
    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(playerState(playerId).ticketCount() <= 0);

        Map<PlayerId, PlayerState> newPlayerState = new EnumMap<>(playerState);
        newPlayerState.put(
                playerId,
                playerState(playerId).withAddedTickets(chosenTickets)
        );

        return new GameState(tickets, cardState, currentPlayerId(), newPlayerState, lastPlayer());
    }
    
    /**
     * Retourne un état identique au récepteur, mais dans lequel le joueur courant a tiré
     * les billets <code>drawnTickets</code> du sommet de la pioche,
     * et choisi de garder ceux contenus dans <code>chosenTickets</code>.
     * 
     * @param drawnTickets
     *          les billets tirés par le joueur
     * @param chosenTickets
     *          les billets que le joueur a choisi de garder
     * @throws IllegalArgumentException
     *          si l'ensemble des billets gardés n'est pas inclus dans celui des billets tirés
     * @return
     *          un état identique au récepteur mais dans lequel
     *          le joueur courant a en plus les billets de <code>chosenTickets</code>
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));

        Map<PlayerId, PlayerState> newPlayerState = new EnumMap<>(playerState);
        newPlayerState.put(
                currentPlayerId(),
                currentPlayerState().withAddedTickets(chosenTickets)
        );

        return new GameState(
                tickets.withoutTopCards(drawnTickets.size()),
                cardState,
                currentPlayerId(),
                newPlayerState,
                lastPlayer()
        );
    }
    
    /**
     * Retourne un état identique au récepteur si ce n'est que la carte face retournée à l'emplacement donné
     * a été placée dans la main du joueur courant, et remplacée par celle au sommet de la pioche.
     * 
     * @param slot
     *          l'emplacement de la carte face retournée en question
     * @throws IllegalArgumentException
     *          s'il n'est pas possible de tirer des cartes
     * @return
     *          un état identique au récepteur mais la carte en question a été placée
     *          dans la main du joueur courant
     */
    public GameState withDrawnFaceUpCard(int slot) {
        Preconditions.checkArgument(canDrawCards());

        Map<PlayerId, PlayerState> newPlayerState = new EnumMap<>(playerState);
        newPlayerState.put(
                currentPlayerId(),
                currentPlayerState().withAddedCard(cardState.faceUpCard(slot))
        );

        return new GameState(
                tickets,
                cardState.withDrawnFaceUpCard(slot),
                currentPlayerId(),
                newPlayerState,
                lastPlayer()
        );
    }
    
    /**
     * Retourne un état identique au récepteur, si ce n'est que la carte du sommet de la pioche
     * a été placée dans la main du joueur courant.
     * 
     * @throws IllegalArgumentException
     *          s'il n'est pas possible de tirer des cartes
     * @return
     *          un état identique au récepteur où la carte du sommet de la pioche
     *          a été placée dans la main du joueur courant
     */
    public GameState withBlindlyDrawnCard() {
        Preconditions.checkArgument(canDrawCards());

        Map<PlayerId, PlayerState> newPlayerState = new EnumMap<>(playerState);
        newPlayerState.put(
                currentPlayerId(),
                currentPlayerState().withAddedCard(cardState.topDeckCard())
        );

        return new GameState(
                tickets,
                cardState.withoutTopDeckCard(),
                currentPlayerId(),
                newPlayerState,
                lastPlayer()
        );
    }
    
    /**
     * Retourne un état identique au récepteur mais dans lequel le joueur courant
     * s'est emparé de la route donnée au moyen des cartes données.
     * 
     * @param route
     *          la route dont le joueur courant s'est emparé
     * @param cards
     *          les cartes au moyen desquelles le joueur courant
     *          s'est emparé de la route donnée
     * @return
     *          un état identique au récepteur où le joueur courant
     *          s'est emparé de la route donnée au moyen des cartes données
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards) {
        Map<PlayerId, PlayerState> newPlayerState = new EnumMap<>(playerState);
        newPlayerState.put(
                currentPlayerId(),
                currentPlayerState().withClaimedRoute(route, cards)
        );

        return new GameState(
                tickets,
                cardState.withMoreDiscardedCards(cards),
                currentPlayerId(),
                newPlayerState,
                lastPlayer()
        );
    }
    
    // MARK:- Dernier tour et fin d'un tour
    
    /**
     * Retourne vrai si et seulement si le dernier tour commence,
     * c'est-à-dire si l'identité du dernier joueur est actuellement inconnue
     * mais que le joueur courant n'a plus que deux wagons ou moins.
     * Cette méthode doit être appelée uniquement à la fin du tour d'un joueur.
     * 
     * @return
     *          vrai si et seulement si le dernier tour commence
     */
    public boolean lastTurnBegins() {
        return lastPlayer() == null && currentPlayerState().carCount() <= 2;
    }
    
    /**
     * Termine le tour du joueur courant.
     * 
     * @return
     *          un état identique au récepteur si ce n'est que le joueur courant
     *          est celui qui suit le joueur courant actuel, et si <code>lastTurnBegins</code>
     *          retourne vrai, le joueur courant actuel devient le dernier joueur
     */
    public GameState forNextTurn() {
        return new GameState(
                tickets,
                cardState,
                currentPlayerId().next(),
                playerState,
                lastTurnBegins() ? currentPlayerId() : null
        );
    }

}