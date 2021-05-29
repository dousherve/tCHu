package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import static ch.epfl.tchu.net.Serde.*;

/**
 * Classe publique, finale, immuable et non instanciable contenant
 * la totalité des serdes utiles à la sérialisation/désérialisation
 * de différents objets composant le jeu de tCHu.
 *
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
public final class Serdes {

    private Serdes() {}
    
    private static Map<PlayerId, PublicPlayerState> deserializePublicPlayerState(String[] source, int offset) {
        Map<PlayerId, PublicPlayerState> playerState = new EnumMap<>(PlayerId.class);
        for (int i = 0; i < PlayerId.COUNT; ++i) {
            playerState.put(
                    PlayerId.ALL.get(i),
                    PUBLIC_PLAYER_STATE.deserialize(source[offset + i])
            );
        }
        
        return playerState;
    }

    // MARK:- Serdes de types simples
    
    /**
     * Serde capable de sérialiser/désérialiser des entiers.
     */
    public static final Serde<Integer> INTEGER = Serde.of(String::valueOf, Integer::parseInt);
    
    /**
     * Serde capable de sérialiser/désérialiser des chaînes de caractères.
     */
    public static final Serde<String> STRING = Serde.of(
            raw -> Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8)),
            s -> new String(Base64.getDecoder().decode(s), StandardCharsets.UTF_8)
    );
    
    /**
     * Serde capable de sérialiser/désérialiser des identités de joueur.
     */
    public static final Serde<PlayerId> PLAYER_ID = Serde.oneOf(PlayerId.ALL);
    
    /**
     * Serde capable de sérialiser/désérialiser des actions qu'un joueur peut effectuer.
     */
    public static final Serde<Player.TurnKind> TURN_KIND = Serde.oneOf(Player.TurnKind.ALL);
    
    /**
     * Serde capable de sérialiser/désérialiser des cartes.
     */
    public static final Serde<Card> CARD = Serde.oneOf(Card.ALL);
    
    /**
     * Serde capable de sérialiser/désérialiser des routes.
     */
    public static final Serde<Route> ROUTE = Serde.oneOf(ChMap.routes());
    
    /**
     * Serde capable de sérialiser/désérialiser des billets.
     */
    public static final Serde<Ticket> TICKET = Serde.oneOf(ChMap.tickets());

    // MARK:- Serdes de collections
    
    /**
     * Serde capable de sérialiser/désérialiser des listes de chaînes de caractères.
     */
    public static final Serde<List<String>> STRING_LIST = Serde.listOf(STRING);
    
    /**
     * Serde capable de sérialiser/désérialiser des listes de cartes.
     */
    public static final Serde<List<Card>> CARD_LIST = Serde.listOf(CARD);
    
    /**
     * Serde capable de sérialiser/désérialiser des listes de routes.
     */
    public static final Serde<List<Route>> ROUTE_LIST = Serde.listOf(ROUTE);
    
    /**
     * Serde capable de sérialiser/désérialiser des multiensembles triés de cartes.
     */
    public static final Serde<SortedBag<Card>> CARD_BAG = Serde.bagOf(CARD);
    
    /**
     * Serde capable de sérialiser/désérialiser des multiensembles triés de billets.
     */
    public static final Serde<SortedBag<Ticket>> TICKET_BAG = Serde.bagOf(TICKET);
    
    /**
     * Serde capable de sérialiser/désérialiser des listes de multiensembles triés de cartes.
     */
    public static final Serde<List<SortedBag<Card>>> CARD_BAG_LIST = Serde.listOf(CARD_BAG, SEMI_COLON);
    
    // MARK:- Serdes de types composites
    
    /**
     * Serde capable de sérialiser/désérialiser des états publics de cartes.
     */
    public static final Serde<PublicCardState> PUBLIC_CARD_STATE = Serde.of(
            state -> String.join(SEMI_COLON,
                    CARD_LIST.serialize(state.faceUpCards()),
                    INTEGER.serialize(state.deckSize()),
                    INTEGER.serialize(state.discardsSize())
            ),
            serialized -> {
                String[] tokens = split(serialized);
                int i = 0;
                return new PublicCardState(
                        CARD_LIST.deserialize(tokens[i++]),
                        INTEGER.deserialize(tokens[i++]),
                        INTEGER.deserialize(tokens[i])
                );
            }
    );
    
    /**
     * Serde capable de sérialiser/désérialiser des états publics de joueur.
     */
    public static final Serde<PublicPlayerState> PUBLIC_PLAYER_STATE = Serde.of(
            state -> String.join(SEMI_COLON,
                    INTEGER.serialize(state.ticketCount()),
                    INTEGER.serialize(state.cardCount()),
                    ROUTE_LIST.serialize(state.routes())
            ),
            serialized -> {
                String[] tokens = split(serialized);
                int i = 0;
                return new PublicPlayerState(
                        INTEGER.deserialize(tokens[i++]),
                        INTEGER.deserialize(tokens[i++]),
                        ROUTE_LIST.deserialize(tokens[i])
                );
            }
    );
    
    /**
     * Serde capable de sérialiser/désérialiser des états complets de joueur.
     */
    public static final Serde<PlayerState> PLAYER_STATE = Serde.of(
            state -> String.join(SEMI_COLON,
                    TICKET_BAG.serialize(state.tickets()),
                    CARD_BAG.serialize(state.cards()),
                    ROUTE_LIST.serialize(state.routes())
            ),
            serialized -> {
                String[] tokens = split(serialized);
                int i = 0;
                return new PlayerState(
                        TICKET_BAG.deserialize(tokens[i++]),
                        CARD_BAG.deserialize(tokens[i++]),
                        ROUTE_LIST.deserialize(tokens[i])
                );
            }
    );
    
    /**
     * Serde capable de sérialiser/désérialiser des états publics de jeu.
     */
    public static final Serde<PublicGameState> PUBLIC_GAME_STATE = Serde.of(
            state -> {
                StringJoiner sj = new StringJoiner(COLON);
                
                sj.add(INTEGER.serialize(state.ticketsCount()));
                sj.add(PUBLIC_CARD_STATE.serialize(state.cardState()));
                sj.add(PLAYER_ID.serialize(state.currentPlayerId()));
                for (PlayerId id : PlayerId.ALL)
                    sj.add(PUBLIC_PLAYER_STATE.serialize(state.playerState(id)));
                sj.add(PLAYER_ID.serialize(state.lastPlayer()));
                
                return sj.toString();
            },
            serialized -> {
                String[] tokens = split(serialized, COLON);
                int i = 0;
                return new PublicGameState(
                        INTEGER.deserialize(tokens[i++]),
                        PUBLIC_CARD_STATE.deserialize(tokens[i++]),
                        PLAYER_ID.deserialize(tokens[i++]),
                        deserializePublicPlayerState(tokens, i),
                        PLAYER_ID.deserialize(tokens[i + PlayerId.COUNT])
                );
            }
    );

}
