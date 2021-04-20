package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.Ticket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;
import static ch.epfl.tchu.net.Serde.split;
import static java.nio.charset.StandardCharsets.US_ASCII;

public final class RemotePlayerClient {
    
    private static final String SPACE = " ";
    
    private final Player player;
    private final Socket socket;
    
    private <T> void sendResponse(Serde<T> serde, T response) {
        try {
            BufferedWriter w =
                    new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream(), US_ASCII));
            
            w.write(serde.serialize(response));
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    public RemotePlayerClient(Player player, String host, int port) {
        this.player = player;
        try {
            this.socket = new Socket(host, port);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    public void run() {
        try (BufferedReader r =
                     new BufferedReader(
                             new InputStreamReader(socket.getInputStream(), US_ASCII))
        ) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] split = split(line, SPACE);
                MessageId message = MessageId.valueOf(split[0]);
    
                switch (message) {
                    case INIT_PLAYERS:
                        PlayerId ownId = Serdes.PLAYER_ID.deserialize(split[1]);
                        List<String> names = Serdes.STRING_LIST.deserialize(split[2]);
                        // TODO: Optimiser 
                        final Map<PlayerId, String> playerNames = new EnumMap<>(PlayerId.class);
                        playerNames.put(PLAYER_1, names.get(0));
                        playerNames.put(PLAYER_2, names.get(1));
                        player.initPlayers(ownId, playerNames);
                        break;
                        
                    case RECEIVE_INFO:
                        player.receiveInfo(Serdes.STRING.deserialize(split[1]));
                        break;
                        
                    case UPDATE_STATE:
                        player.updateState(
                                Serdes.PUBLIC_GAME_STATE.deserialize(split[1]),
                                Serdes.PLAYER_STATE.deserialize(split[2])
                        );
                        break;
                        
                    case SET_INITIAL_TICKETS:
                        player.setInitialTicketChoice(Serdes.TICKET_BAG.deserialize(split[1]));
                        break;
                        
                    case CHOOSE_INITIAL_TICKETS:
                        sendResponse(Serdes.TICKET_BAG, player.chooseInitialTickets());
                        break;
                        
                    case NEXT_TURN:
                        sendResponse(Serdes.TURN_KIND, player.nextTurn());
                        break;
                        
                    case CHOOSE_TICKETS:
                        final SortedBag<Ticket> ticketOptions = Serdes.TICKET_BAG.deserialize(split[1]);
                        sendResponse(Serdes.TICKET_BAG, player.chooseTickets(ticketOptions));
                        break;
                        
                    case DRAW_SLOT:
                        sendResponse(Serdes.INTEGER, player.drawSlot());
                        break;
                        
                    case ROUTE:
                        sendResponse(Serdes.ROUTE, player.claimedRoute());
                        break;
                        
                    case CARDS:
                        sendResponse(Serdes.CARD_BAG, player.initialClaimCards());
                        break;
                        
                    case CHOOSE_ADDITIONAL_CARDS:
                        final List<SortedBag<Card>> cardOptions = Serdes.CARD_BAG_LIST.deserialize(split[1]);
                        sendResponse(Serdes.CARD_BAG, player.chooseAdditionalCards(cardOptions));
                        break;
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
}
