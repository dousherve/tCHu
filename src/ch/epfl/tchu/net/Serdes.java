package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public final class Serdes {

    private Serdes() {}

    public static final Serde<Integer> INTEGER = Serde.of(String::valueOf, Integer::parseInt);

    public static final Serde<String> STRING = Serde.of(
            s -> Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8)),
            s -> new String(Base64.getDecoder().decode(s), StandardCharsets.UTF_8));

    public static final Serde<PlayerId> PLAYER_ID = Serde.oneOf(PlayerId.ALL);
    public static final Serde<Player.TurnKind> TURN_KIND = Serde.oneOf(Player.TurnKind.ALL);
    public static final Serde<Card> CARD = Serde.oneOf(Card.ALL);
    public static final Serde<Route> ROUTE = Serde.oneOf(ChMap.routes());
    public static final Serde<Ticket> TICKET = Serde.oneOf(ChMap.tickets());

    public static final Serde<List<String>> STRING_LIST = Serde.listOf(STRING, ",");
    public static final Serde<List<Card>> CARD_LIST = Serde.listOf(CARD, ",");
    public static final Serde<List<Route>> ROUTE_LIST = Serde.listOf(ROUTE, ",");
    public static final Serde<SortedBag<Card>> CARD_BAG = Serde.bagOf(CARD, ",");
    public static final Serde<SortedBag<Ticket>> TICKET_BAG = Serde.bagOf(TICKET, ",");
    public static final Serde<List<SortedBag<Card>>> CARD_BAG_LIST = Serde.listOf(CARD_BAG, ";");



    //public static final Serde<PublicCardState> PUBLIC_CARD_STATE = Serde.listOf(TICKET, ";");





}
