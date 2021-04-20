package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.net.Serde.*;

public final class Serdes {

    private Serdes() {}

    // MARK:- Serdes de types primaires
    
    public static final Serde<Integer> INTEGER = Serde.of(String::valueOf, Integer::parseInt);
    public static final Serde<String> STRING = Serde.of(
            raw -> Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8)),
            s -> new String(Base64.getDecoder().decode(s), StandardCharsets.UTF_8)
    );
    public static final Serde<PlayerId> PLAYER_ID = Serde.oneOf(PlayerId.ALL);
    public static final Serde<Player.TurnKind> TURN_KIND = Serde.oneOf(Player.TurnKind.ALL);
    public static final Serde<Card> CARD = Serde.oneOf(Card.ALL);
    public static final Serde<Route> ROUTE = Serde.oneOf(ChMap.routes());
    public static final Serde<Ticket> TICKET = Serde.oneOf(ChMap.tickets());

    // MARK:- Serdes de collections
    
    public static final Serde<List<String>> STRING_LIST = Serde.listOf(STRING);
    public static final Serde<List<Card>> CARD_LIST = Serde.listOf(CARD);
    public static final Serde<List<Route>> ROUTE_LIST = Serde.listOf(ROUTE);
    public static final Serde<SortedBag<Card>> CARD_BAG = Serde.bagOf(CARD);
    public static final Serde<SortedBag<Ticket>> TICKET_BAG = Serde.bagOf(TICKET);
    public static final Serde<List<SortedBag<Card>>> CARD_BAG_LIST = Serde.listOf(CARD_BAG, SEMI_COLON);
    
    // MARK:- Serdes de types composites
    
    public static final Serde<PublicCardState> PUBLIC_CARD_STATE = Serde.of(
            raw -> String.join(SEMI_COLON,
                    CARD_LIST.serialize(raw.faceUpCards()),
                    INTEGER.serialize(raw.deckSize()),
                    INTEGER.serialize(raw.discardsSize())
            ),
            serialized -> {
                final String[] split = split(serialized);
                return new PublicCardState(
                        CARD_LIST.deserialize(split[0]),
                        INTEGER.deserialize(split[1]),
                        INTEGER.deserialize(split[2])
                );
            }
    );
    
    public static final Serde<PublicPlayerState> PUBLIC_PLAYER_STATE = Serde.of(
            raw -> String.join(SEMI_COLON,
                    INTEGER.serialize(raw.ticketCount()),
                    INTEGER.serialize(raw.cardCount()),
                    ROUTE_LIST.serialize(raw.routes())
            ),
            serialized -> {
                final String[] split = split(serialized);
                return new PublicPlayerState(
                        INTEGER.deserialize(split[0]),
                        INTEGER.deserialize(split[1]),
                        ROUTE_LIST.deserialize(split[2])
                );
            }
    );
    
    public static final Serde<PlayerState> PLAYER_STATE = Serde.of(
            raw -> String.join(SEMI_COLON,
                    TICKET_BAG.serialize(raw.tickets()),
                    CARD_BAG.serialize(raw.cards()),
                    ROUTE_LIST.serialize(raw.routes())
            ),
            serialized -> {
                final String[] split = split(serialized);
                return new PlayerState(
                        TICKET_BAG.deserialize(split[0]),
                        CARD_BAG.deserialize(split[1]),
                        ROUTE_LIST.deserialize(split[2])
                );
            }
    );
    
    public static final Serde<PublicGameState> PUBLIC_GAME_STATE = Serde.of(
            raw -> String.join(COLON,
                    INTEGER.serialize(raw.ticketsCount()),
                    PUBLIC_CARD_STATE.serialize(raw.cardState()),
                    PLAYER_ID.serialize(raw.currentPlayerId()),
                    PUBLIC_PLAYER_STATE.serialize(raw.playerState(PlayerId.PLAYER_1)),
                    PUBLIC_PLAYER_STATE.serialize(raw.playerState(PlayerId.PLAYER_2)),
                    PLAYER_ID.serialize(raw.lastPlayer())
            ),
            serialized -> {
                final String[] split = split(serialized, COLON);
    
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
    );

}
