package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public final class Serdes {

    private Serdes() {}
    
    public static final String COMMA = ",";
    public static final String SEMI_COLON = ";";
    public static final String COLON = ":";

    public static final Serde<Integer> INTEGER = Serde.of(String::valueOf, Integer::parseInt);
    public static final Serde<String> STRING = Serde.of(
            s -> Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8)),
            s -> new String(Base64.getDecoder().decode(s), StandardCharsets.UTF_8));
    public static final Serde<PlayerId> PLAYER_ID = Serde.oneOf(PlayerId.ALL);
    public static final Serde<Player.TurnKind> TURN_KIND = Serde.oneOf(Player.TurnKind.ALL);
    public static final Serde<Card> CARD = Serde.oneOf(Card.ALL);
    public static final Serde<Route> ROUTE = Serde.oneOf(ChMap.routes());
    public static final Serde<Ticket> TICKET = Serde.oneOf(ChMap.tickets());

    public static final Serde<List<String>> STRING_LIST = Serde.listOf(STRING, COMMA);
    public static final Serde<List<Card>> CARD_LIST = Serde.listOf(CARD, COMMA);
    public static final Serde<List<Route>> ROUTE_LIST = Serde.listOf(ROUTE, COMMA);
    public static final Serde<SortedBag<Card>> CARD_BAG = Serde.bagOf(CARD, COMMA);
    public static final Serde<SortedBag<Ticket>> TICKET_BAG = Serde.bagOf(TICKET, COMMA);
    public static final Serde<List<SortedBag<Card>>> CARD_BAG_LIST = Serde.listOf(CARD_BAG, SEMI_COLON);
    
    public static final Serde<PublicCardState> PUBLIC_CARD_STATE = new Serde<>() {
        
        // private final List<Serde> serdes = List.of(CARD_LIST, INTEGER, INTEGER);
        
        @Override
        public String serialize(PublicCardState raw) {
            return String.join(SEMI_COLON,
                    CARD_LIST.serialize(raw.faceUpCards()),
                    INTEGER.serialize(raw.deckSize()),
                    INTEGER.serialize(raw.discardsSize())
            );
        }
    
        @Override
        public PublicCardState deserialize(String serialized) {
            String[] split = serialized.split(Pattern.quote(SEMI_COLON), -1);
            return new PublicCardState(
                    CARD_LIST.deserialize(split[0]),
                    INTEGER.deserialize(split[1]),
                    INTEGER.deserialize(split[2])
            );
        }
    };
    
    public static final Serde<PublicPlayerState> PUBLIC_PLAYER_STATE = new Serde<>() {
        @Override
        public String serialize(PublicPlayerState raw) {
            return String.join(SEMI_COLON,
                    INTEGER.serialize(raw.ticketCount()),
                    INTEGER.serialize(raw.cardCount()),
                    ROUTE_LIST.serialize(raw.routes())
            );
        }
    
        @Override
        public PublicPlayerState deserialize(String serialized) {
            String[] split = serialized.split(Pattern.quote(SEMI_COLON), -1);
            return new PublicPlayerState(
                    INTEGER.deserialize(split[0]), 
                    INTEGER.deserialize(split[1]), 
                    ROUTE_LIST.deserialize(split[2]) 
            );
        }
    };
    
    public static final Serde<PlayerState> PLAYER_STATE = new Serde<>() {
        @Override
        public String serialize(PlayerState raw) {
            return String.join(SEMI_COLON,
                    TICKET_BAG.serialize(raw.tickets()),
                    CARD_BAG.serialize(raw.cards()),
                    ROUTE_LIST.serialize(raw.routes())
            );
        }
    
        @Override
        public PlayerState deserialize(String serialized) {
            String[] split = serialized.split(Pattern.quote(SEMI_COLON), -1);
            return new PlayerState(
                    TICKET_BAG.deserialize(split[0]),
                    CARD_BAG.deserialize(split[1]),
                    ROUTE_LIST.deserialize(split[2])
            );
        }
    };
    
    public static final Serde<PublicGameState> PUBLIC_GAME_STATE = new Serde<>() {
        @Override
        public String serialize(PublicGameState raw) {
            return String.join(COLON,
                    INTEGER.serialize(raw.ticketsCount()),
                    PUBLIC_CARD_STATE.serialize(raw.cardState()),
                    PLAYER_ID.serialize(raw.currentPlayerId()),
                    PUBLIC_PLAYER_STATE.serialize(raw.playerState(PlayerId.PLAYER_1)),
                    PUBLIC_PLAYER_STATE.serialize(raw.playerState(PlayerId.PLAYER_2)),
                    PLAYER_ID.serialize(raw.lastPlayer())
            );
        }
    
        @Override
        public PublicGameState deserialize(String serialized) {
            String[] split = serialized.split(Pattern.quote(COLON), -1);
            
            final Map<PlayerId, PublicPlayerState> playerState = new EnumMap<>(PlayerId.class);
            playerState.put(PlayerId.PLAYER_1, PUBLIC_PLAYER_STATE.deserialize(split[3]));
            playerState.put(PlayerId.PLAYER_2, PUBLIC_PLAYER_STATE.deserialize(split[4]));
            
            return new PublicGameState(
                    INTEGER.deserialize(split[0]),
                    PUBLIC_CARD_STATE.deserialize(split[1]),
                    PLAYER_ID.deserialize(split[2]),
                    playerState,
                    PLAYER_ID.deserialize(split[5]) 
            );
        }
    };

}
