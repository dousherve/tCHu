package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;
import static ch.epfl.tchu.net.MessageId.*;
import static java.nio.charset.StandardCharsets.US_ASCII;

public final class RemotePlayerProxy implements Player {
    
    private static final String SPACE = " ";
    
    private Socket socket;
    
    private void sendMessage(MessageId messageId, String message) {
        try {
            BufferedWriter w =
                    new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream(), US_ASCII));
                    
            w.write(messageId.name());
            if (message != null && ! message.isEmpty()) {
                w.write(SPACE);
                w.write(message);
            }
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    private void sendMessage(MessageId messageId) {
        sendMessage(messageId, null);
    }
    
    private String receiveMessage() {
        try {
            BufferedReader r =
                    new BufferedReader(
                            new InputStreamReader(socket.getInputStream(), US_ASCII));
                    
            return r.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    public RemotePlayerProxy(Socket socket) {
        this.socket = socket;
    }
    
    // MARK:- Méthodes de Player
    
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        sendMessage(INIT_PLAYERS, String.join(SPACE,
                Serdes.PLAYER_ID.serialize(ownId),
                Serdes.STRING_LIST.serialize(
                        List.of(playerNames.get(PLAYER_1), playerNames.get(PLAYER_2))
                )
        ));
    }
    
    @Override
    public void receiveInfo(String info) {
        sendMessage(RECEIVE_INFO, Serdes.STRING.serialize(info));
    }
    
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        sendMessage(UPDATE_STATE, String.join(SPACE,
                Serdes.PUBLIC_GAME_STATE.serialize(newState),
                Serdes.PLAYER_STATE.serialize(ownState)
        ));
    }
    
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        sendMessage(SET_INITIAL_TICKETS, Serdes.TICKET_BAG.serialize(tickets));
    }
    
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        sendMessage(CHOOSE_INITIAL_TICKETS);
        return Serdes.TICKET_BAG.deserialize(receiveMessage());
    }
    
    @Override
    public TurnKind nextTurn() {
        sendMessage(NEXT_TURN);
        return Serdes.TURN_KIND.deserialize(receiveMessage());
    }
    
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        sendMessage(CHOOSE_TICKETS, Serdes.TICKET_BAG.serialize(options));
        return Serdes.TICKET_BAG.deserialize(receiveMessage());
    }
    
    @Override
    public int drawSlot() {
        sendMessage(DRAW_SLOT);
        return Serdes.INTEGER.deserialize(receiveMessage());
    }
    
    @Override
    public Route claimedRoute() {
        sendMessage(ROUTE);
        return Serdes.ROUTE.deserialize(receiveMessage());
    }
    
    @Override
    public SortedBag<Card> initialClaimCards() {
        sendMessage(CARDS);
        return Serdes.CARD_BAG.deserialize(receiveMessage());
    }
    
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        sendMessage(CHOOSE_ADDITIONAL_CARDS, Serdes.CARD_BAG_LIST.serialize(options));
        return Serdes.CARD_BAG.deserialize(receiveMessage());
    }
    
}
