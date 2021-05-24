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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static ch.epfl.tchu.net.MessageId.*;
import static ch.epfl.tchu.net.Serde.SPACE;
import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * Classe publique, finale et immuable représentant un mandataire de joueur distant.
 * Elle implémente l'interface <code>{@link Player}</code>
 * et peut ainsi jouer le rôle d'un joueur.
 * 
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
public final class RemotePlayerProxy implements Player {
    
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
    
    private <T> T receiveResponse(Serde<T> serde) {
        try {
            BufferedReader r =
                    new BufferedReader(
                            new InputStreamReader(socket.getInputStream(), US_ASCII));
        
            return serde.deserialize(r.readLine());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    /**
     * Construit un mandataire de joueur distant en utilisant
     * le <code>{@link Socket}</code> donné.
     * 
     * @param socket
     *          le socket utilisé pour communiquer à travers le réseau
     *          avec le client par échange de messages textuels
     * @throws NullPointerException
     *          si l'instance de {@link Socket} donnée
     *          vaut <code>null</code>
     */
    public RemotePlayerProxy(Socket socket) {
        this.socket = Objects.requireNonNull(socket);
    }
    
    // MARK:- Méthodes de Player
    
    /*
        Pour chacune des méthodes de Player, on sérialise ses arguments
        et on envoie le message au serveur.
        Si la méthode retroune une valeur,
        on la récupère depuis le réseau sous forme sérialisée,
        puis on la désérialise avant de la retourner.
     */
    
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        sendMessage(INIT_PLAYERS, String.join(SPACE,
                Serdes.PLAYER_ID.serialize(ownId),
                Serdes.STRING_LIST.serialize(new ArrayList<>(playerNames.values()))
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
        return receiveResponse(Serdes.TICKET_BAG);
    }
    
    @Override
    public TurnKind nextTurn() {
        sendMessage(NEXT_TURN);
        return receiveResponse(Serdes.TURN_KIND);
    }
    
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        sendMessage(CHOOSE_TICKETS, Serdes.TICKET_BAG.serialize(options));
        return receiveResponse(Serdes.TICKET_BAG);
    }
    
    @Override
    public int drawSlot() {
        sendMessage(DRAW_SLOT);
        return receiveResponse(Serdes.INTEGER);
    }
    
    @Override
    public Route claimedRoute() {
        sendMessage(ROUTE);
        return receiveResponse(Serdes.ROUTE);
    }
    
    @Override
    public SortedBag<Card> initialClaimCards() {
        sendMessage(CARDS);
        return receiveResponse(Serdes.CARD_BAG);
    }
    
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        sendMessage(CHOOSE_ADDITIONAL_CARDS, Serdes.CARD_BAG_LIST.serialize(options));
        return receiveResponse(Serdes.CARD_BAG);
    }
    
}
