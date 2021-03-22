package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

public final class GameState extends PublicGameState {
    
    private final Deck<Ticket> tickets;
    private final CardState cardState;
    private final Map<PlayerId, PlayerState> playerState;
    
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
        
        return new GameState(Deck.of(tickets, rng), CardState.of(deckCards), firstPlayerId, playerState);
    }
    
    private GameState(Deck<Ticket> tickets, CardState cardState, PlayerId currentPlayerId, Map<PlayerId, PlayerState> playerState, PlayerId lastPlayer) {
        super(tickets.size(), cardState, currentPlayerId, Map.copyOf(playerState), lastPlayer);
        
        this.tickets = tickets;
        this.cardState = cardState;
        this.playerState = Collections.unmodifiableMap(playerState);
    }

    private GameState(Deck<Ticket> tickets, CardState cardState, PlayerId currentPlayerId, Map<PlayerId, PlayerState> playerState) {
        this(tickets, cardState, currentPlayerId, playerState, null);
    }
    
    @Override
    public PlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }
    
    @Override
    public PlayerState currentPlayerState() {
        return playerState(currentPlayerId());
    }
    
    // MARK:- Billets et cartes

    public SortedBag<Ticket> topTickets(int count) {
        return tickets.topCards(count);
    }

    public GameState withoutTopTickets(int count) {
        return new GameState(tickets.withoutTopCards(count), cardState, currentPlayerId(), playerState);
    }

    public Card topCard() {
        return cardState.topDeckCard();
    }

    public GameState withoutTopCard() {
        return new GameState(tickets, cardState.withoutTopDeckCard(), currentPlayerId(), playerState);
    }

    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) {
        return new GameState(tickets, cardState.withMoreDiscardedCards(discardedCards), currentPlayerId(), playerState);
    }

    public GameState withCardsDeckRecreatedIfNeeded(Random rng) {
        return cardState.isDeckEmpty()
                ? new GameState(
                        tickets,
                        cardState.withDeckRecreatedFromDiscards(rng),
                        currentPlayerId(),
                        playerState
                ) : this;
    }
    
    // MARK:- États dérivés de l'état courant

    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(playerState(playerId).ticketCount() <= 0);

        Map<PlayerId, PlayerState> newPlayerState = new EnumMap<>(playerState);
        newPlayerState.put(
                playerId,
                playerState(playerId).withAddedTickets(chosenTickets)
        );

        return new GameState(tickets, cardState, currentPlayerId(), newPlayerState);
    }

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
                newPlayerState
        );
    }

    public GameState withDrawnFaceUpCard(int slot) {
        Preconditions.checkArgument(canDrawCards());

        Map<PlayerId, PlayerState> newPlayerState = new EnumMap<>(playerState);
        newPlayerState.put(
                currentPlayerId(),
                currentPlayerState().withAddedCard(cardState.faceUpCard(slot))
        );

        return new GameState(tickets, cardState.withDrawnFaceUpCard(slot), currentPlayerId(), newPlayerState);
    }

    public GameState withBlindlyDrawnCard() {
        Preconditions.checkArgument(canDrawCards());

        Map<PlayerId, PlayerState> newPlayerState = new EnumMap<>(playerState);
        newPlayerState.put(
                currentPlayerId(),
                currentPlayerState().withAddedCard(cardState.topDeckCard())
        );

        return new GameState(tickets, cardState.withoutTopDeckCard(), currentPlayerId(), newPlayerState);
    }

    public GameState withClaimedRoute(Route route, SortedBag<Card> cards) {
        Map<PlayerId, PlayerState> newPlayerState = new EnumMap<>(playerState);
        newPlayerState.put(
                currentPlayerId(),
                currentPlayerState().withClaimedRoute(route, cards)
        );

        return new GameState(tickets, cardState.withMoreDiscardedCards(cards), currentPlayerId(), newPlayerState);
    }
    
    // MARK:- Dernier tour, fin d'un tour

    public boolean lastTurnBegins() {
        return lastPlayer() == null && currentPlayerState().carCount() <= 2;
    }

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