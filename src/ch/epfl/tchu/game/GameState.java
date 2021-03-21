package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

public final class GameState extends PublicGameState {
    
    private final SortedBag<Ticket> tickets;
    private final CardState cardState;
    private final Map<PlayerId, PlayerState> playerState;
    
    private static Map<PlayerId, PublicPlayerState> makePublic(Map<PlayerId, PlayerState> playerState) {
        EnumMap<PlayerId, PublicPlayerState> publicPlayerState = new EnumMap<>(PlayerId.class);
        for (var entry : playerState.entrySet())
            publicPlayerState.put(entry.getKey(), entry.getValue());
        
        return Collections.unmodifiableMap(publicPlayerState);
    }
    
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
    
    private GameState(SortedBag<Ticket> tickets, CardState cardState, PlayerId currentPlayerId, Map<PlayerId, PlayerState> playerState) {
        super(tickets.size(), cardState, currentPlayerId, makePublic(playerState), null);
        
        this.tickets = tickets;
        this.cardState = cardState;
        this.playerState = playerState;
    }
    
    @Override
    public PlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }
    
    @Override
    public PlayerState currentPlayerState() {
        return playerState(currentPlayerId());
    }
    
}
