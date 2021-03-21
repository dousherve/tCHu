package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

public final class GameState extends PublicGameState {
    
    private final SortedBag<Ticket> tickets;
    private final CardState cardState;
    private final Map<PlayerId, PlayerState> playerState;
    
    public static GameState initial(SortedBag<Ticket> tickets, Random rng) {
        final Deck<Card> ALL_CARDS = Deck.of(Constants.ALL_CARDS, rng); 
        final Deck<Card> DECK_CARDS = ALL_CARDS.withoutTopCards(8);
        
        final PlayerId FIRST_PLAYER_ID = PlayerId.ALL.get(rng.nextInt(PlayerId.COUNT));
        final PlayerId SECOND_PLAYER_ID = FIRST_PLAYER_ID.next();
        
        final PlayerState FIRST_PLAYER_STATE = PlayerState.initial(ALL_CARDS.topCards(4));
        final PlayerState SECOND_PLAYER_STATE = PlayerState.initial(ALL_CARDS.withoutTopCards(4).topCards(4));
        
        final Map<PlayerId, PlayerState> PLAYER_STATE = Map.of(
                FIRST_PLAYER_ID, FIRST_PLAYER_STATE,
                SECOND_PLAYER_ID, SECOND_PLAYER_STATE
        );
        
        return new GameState(tickets, CardState.of(DECK_CARDS), FIRST_PLAYER_ID, PLAYER_STATE);
    }
    
    private GameState(SortedBag<Ticket> tickets, CardState cardState, PlayerId currentPlayerId, Map<PlayerId, PlayerState> playerState, PlayerId lastPlayer) {
        super(tickets.size(), cardState, currentPlayerId, new EnumMap<>(playerState), lastPlayer);
        
        this.tickets = tickets;
        this.cardState = cardState;
        this.playerState = playerState;
    }

    private GameState(SortedBag<Ticket> tickets, CardState cardState, PlayerId currentPlayerId, Map<PlayerId, PlayerState> playerState) {
        this(tickets, cardState, currentPlayerId, new EnumMap<>(playerState), null);
    }
    
    @Override
    public PlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }
    
    @Override
    public PlayerState currentPlayerState() {
        return playerState(currentPlayerId());
    }

    public SortedBag<Ticket> topTickets(int count) {
        Preconditions.checkArgument(count >= 0 && count <= ticketsCount());

        return SortedBag.of(
                tickets.toList().subList(ticketsCount() - count, ticketsCount())
        );
    }

    public GameState withoutTopTickets(int count) {
        return new GameState(tickets.difference(topTickets(count)), cardState, currentPlayerId(), playerState);
    }

    public Card topCard() {
        Preconditions.checkArgument(! cardState.isDeckEmpty());

        return cardState.topDeckCard();
    }

    public GameState withoutTopCard() {
        return new GameState(tickets, cardState.withoutTopDeckCard(), currentPlayerId(), playerState);
    }

    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) {
        return new GameState(tickets, cardState.withMoreDiscardedCards(discardedCards), currentPlayerId(), playerState);
    }

    public GameState withCardsDeckRecreatedIfNeeded(Random rng) {
        return cardState.isDeckEmpty() ?
                new GameState(
                        tickets,
                        cardState.withDeckRecreatedFromDiscards(rng),
                        currentPlayerId(),
                        playerState
                ) : this;
    }

    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(playerState(playerId).ticketCount() <= 0);

        Map<PlayerId, PlayerState> newPlayerState = new EnumMap<>(playerState);
        newPlayerState.put(playerId, playerState(playerId).withAddedTickets(chosenTickets));

        return new GameState(tickets, cardState, currentPlayerId(), newPlayerState);
    }

    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));

        Map<PlayerId, PlayerState> newPlayerState = new EnumMap<>(playerState);
        newPlayerState.put(currentPlayerId(), playerState(currentPlayerId()).withAddedTickets(chosenTickets));

        return new GameState(tickets.difference(drawnTickets), cardState, currentPlayerId(), newPlayerState);
    }

    public GameState withDrawnFaceUpCard(int slot) {
        Preconditions.checkArgument(canDrawCards());

        Map<PlayerId, PlayerState> newPlayerState = new EnumMap<>(playerState);
        newPlayerState.put(
                currentPlayerId(),
                playerState(currentPlayerId()).withAddedCard(cardState.faceUpCard(slot))
        );

        return new GameState(tickets, cardState.withDrawnFaceUpCard(slot), currentPlayerId(), newPlayerState);
    }

    public GameState withBlindlyDrawnCard() {
        Preconditions.checkArgument(canDrawCards());

        Map<PlayerId, PlayerState> newPlayerState = new EnumMap<>(playerState);
        newPlayerState.put(
                currentPlayerId(),
                playerState(currentPlayerId()).withAddedCard(cardState.topDeckCard())
        );

        return new GameState(tickets, cardState.withoutTopDeckCard(), currentPlayerId(), newPlayerState);
    }

    public GameState withClaimedRoute(Route route, SortedBag<Card> cards) {
        Map<PlayerId, PlayerState> newPlayerState = new EnumMap<>(playerState);
        newPlayerState.put(
                currentPlayerId(),
                playerState(currentPlayerId()).withClaimedRoute(route, cards)
        );

        return new GameState(tickets, cardState.withMoreDiscardedCards(cards), currentPlayerId(),newPlayerState);
    }

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