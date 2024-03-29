package ch.epfl.tchu.net;

import ch.epfl.tchu.Preconditions;
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
import java.util.Objects;

import static ch.epfl.tchu.net.Serde.SPACE;
import static ch.epfl.tchu.net.Serde.split;
import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * Classe publique, finale et immuable représentant un client de joueur distant.
 * Elle implémente l'interface {@link Runnable}, son but étant que ses instances
 * soient passées en argument à un nouveau {@link Thread} destiné à
 * gérer la partie logique du jeu en réseau.
 *
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
public final class RemotePlayerClient implements Runnable {
    
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
    
    /**
     * Construit un nouveau client de joueur distant avec les paramètres donnés.
     * 
     * @param player
     *          le joueur auquel l'objet doit fournir un accès distant
     * @param host
     *          le nom d'hôte à utiliser pour se connecter au mandataire
     * @param port
     *          le port à utiliser pour se connecter au mandataire
     * @throws IllegalArgumentException
     *          si le nom d'hôte donné est vide ou contient
     *          uniquement des caractères d'espacement
     * @throws NullPointerException
     *          si le joueur donné vaut <code>null</code>
     */
    public RemotePlayerClient(Player player, String host, int port) {
        Preconditions.checkArgument(! host.isBlank());
        
        this.player = Objects.requireNonNull(player);
        try {
            this.socket = new Socket(host, port);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    /**
     * Méthode principale de lancement du client.
     * 
     * Elle attend un message en provenance du mandataire,
     * puis appelle la méthode correspondante du joueur.
     * Si cette méthode retourne un résultat,
     * elle le renvoie au mandataire en réponse.
     */
    @Override
    public void run() {
        try (BufferedReader r =
                     new BufferedReader(
                             new InputStreamReader(socket.getInputStream(), US_ASCII))
        ) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] split = split(line, SPACE);
                int i = 0, j;
                MessageId message = MessageId.valueOf(split[i++]);
                
                switch (message) {
                    case INIT_PLAYERS:
                        j = i;
                        PlayerId ownId = Serdes.PLAYER_ID.deserialize(split[j++]);
                        List<String> names = Serdes.STRING_LIST.deserialize(split[j]);
                        
                        Map<PlayerId, String> playerNames = new EnumMap<>(PlayerId.class);
                        for (PlayerId id : PlayerId.ALL)
                            playerNames.put(id, names.get(id.ordinal()));
                        
                        player.initPlayers(ownId, playerNames);
                        break;
                        
                    case RECEIVE_INFO:
                        player.receiveInfo(Serdes.STRING.deserialize(split[i]));
                        break;
                        
                    case UPDATE_STATE:
                        j = i;
                        player.updateState(
                                Serdes.PUBLIC_GAME_STATE.deserialize(split[j++]),
                                Serdes.PLAYER_STATE.deserialize(split[j])
                        );
                        break;
                        
                    case SET_INITIAL_TICKETS:
                        player.setInitialTicketChoice(Serdes.TICKET_BAG.deserialize(split[i]));
                        break;
                        
                    case CHOOSE_INITIAL_TICKETS:
                        sendResponse(Serdes.TICKET_BAG, player.chooseInitialTickets());
                        break;
                        
                    case NEXT_TURN:
                        sendResponse(Serdes.TURN_KIND, player.nextTurn());
                        break;
                        
                    case CHOOSE_TICKETS:
                        SortedBag<Ticket> ticketOptions = Serdes.TICKET_BAG.deserialize(split[i]);
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
                        List<SortedBag<Card>> cardOptions = Serdes.CARD_BAG_LIST.deserialize(split[i]);
                        sendResponse(Serdes.CARD_BAG, player.chooseAdditionalCards(cardOptions));
                        break;
                        
                    default:
                        throw new Error();
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
}
