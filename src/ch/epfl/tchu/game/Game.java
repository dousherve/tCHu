package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Classe publique, finale et non instanciable qui représente une partie de tCHu.
 *
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
public final class Game {
    
    private static void broadcastInfo(String info, Map<PlayerId, Player> players) {
        players.forEach((playerId, player) -> player.receiveInfo(info));
    }
    
    private static void broadcastStateChange(GameState newState, Map<PlayerId, Player> players) {
        players.forEach((playerId, player) -> player.updateState(newState, newState.playerState(playerId)));
    }
    
    /**
     * Méthode qui fait jouer une partie de tCHu aux joueurs donnés,
     * dont les noms figurent dans la table <code>playerNames</code>.
     * Les billets disponibles pour cette partie sont ceux de <code>tickets</code>,
     * et le générateur aléatoire <code>rng</code> est utilisé pour créer l'état initial du jeu ainsi que
     * pour mélanger les cartes de la défausse pour en faire une nouvelle pioche quand cela est nécessaire.
     * 
     * @param players
     *          la table associative qui fait correspondre l'identité d'un joueur
     *          à sa représentation dans la partie de tCHu en cours
     * @param playerNames
     *          la table associative qui fait correspondre l'identité d'un joueur
     *          à la chaîne de caractères représentant son nom
     * @param tickets
     *          les billets disponibles de cette partie de tCHu
     * @param rng
     *          le générateur aléatoire utilisé pour créer l'état initial du jeu ainsi que
     *          pour mélanger les cartes de la défausse pour en faire une nouvelle pioche
     * @throws IllegalArgumentException
     *          si l'une des deux tables associatives a une taille différente de 2
     */
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {
        Preconditions.checkArgument(players.size() == 2 && playerNames.size() == 2);
        
        // Initialisation de la partie
        GameState state = GameState.initial(tickets, rng);
        
        players.forEach((playerId, player) -> player.initPlayers(playerId, playerNames));
    
        Map<PlayerId, Info> infos = Map.of(
                state.currentPlayerId(), new Info(playerNames.get(state.currentPlayerId())),
                state.currentPlayerId().next(), new Info(playerNames.get(state.currentPlayerId().next()))
        );
        
        broadcastInfo(infos.get(state.currentPlayerId()).willPlayFirst(), players);
    
        for (Player player : players.values()) {
            player.setInitialTicketChoice(state.topTickets(Constants.INITIAL_TICKETS_COUNT));
            state = state.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
        }
        
        for (Map.Entry<PlayerId, Player> entry : players.entrySet()) {
            final PlayerId id = entry.getKey();
            final Player player = entry.getValue();
            
            broadcastStateChange(state, players);
            state = state.withInitiallyChosenTickets(id, player.chooseInitialTickets());
            player.receiveInfo(infos.get(id).keptTickets(state.playerState(id).ticketCount()));
        }
        
        // Logique d'une partie de tCHu
        boolean isPlaying = true;
        
        while (isPlaying) {
            Player currentPlayer = players.get(state.currentPlayerId());
            
            broadcastStateChange(state, players);
            switch (currentPlayer.nextTurn()) {
                case DRAW_TICKETS:
                    SortedBag<Ticket> drawnTickets = state.topTickets(Constants.IN_GAME_TICKETS_COUNT);
                    SortedBag<Ticket> chosenTickets = currentPlayer.chooseTickets(drawnTickets);
                    state = state.withChosenAdditionalTickets(drawnTickets, chosenTickets);
                    break;
                    
                case DRAW_CARDS:
                    for (int i = 0; i < 2; ++i) {
                        state = state.withCardsDeckRecreatedIfNeeded(rng);
                        // TODO: re-broadcast si recréation ?
                        final int slot = currentPlayer.drawSlot();
                        state = (slot == -1)
                                ? state.withBlindlyDrawnCard()
                                : state.withDrawnFaceUpCard(slot);
                        
                        if (i == 0)
                            broadcastStateChange(state, players);
                    }
                    break;
                    
                case CLAIM_ROUTE:
                    Route route = currentPlayer.claimedRoute();
                    SortedBag<Card> claimCards = currentPlayer.initialClaimCards();
                    
                    SortedBag.Builder<Card> b = new SortedBag.Builder<>();
                    for (int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; ++i) {
                        state = state.withCardsDeckRecreatedIfNeeded(rng);
                        b.add(state.topCard());
                        state = state.withoutTopCard();
                    }
                    SortedBag<Card> drawnCards = b.build();
                    
                    // TODO: À FINIR ET REVOIR; ÇA SENT LE ROUSSI
                    
                    if (route.level() == Route.Level.UNDERGROUND) {
                        int addtitionalCardsCount = route.additionalClaimCardsCount(claimCards, drawnCards);
                        
                        List<SortedBag<Card>> options = state
                                .currentPlayerState()
                                .possibleAdditionalCards(addtitionalCardsCount, claimCards, drawnCards);
                        
                        if (addtitionalCardsCount >= 1 && ! options.isEmpty()) {
                            SortedBag<Card> chosenAdditional = currentPlayer.chooseAdditionalCards(options);
                            if (! chosenAdditional.isEmpty()) {
                                
                            }
                        }
                    } else {
                        state = state
                                .withClaimedRoute(route, claimCards)
                                .withMoreDiscardedCards(drawnCards);
                    }
                    break;
            }
        }
        
    }
    
    private Game() {}
        
}
