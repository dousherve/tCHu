package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.EnumMap;
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
    
    private Game() {}
    
    private static final int IN_GAME_DRAW_CARDS_COUNT = 2;
    
    private static void broadcastInfo(String info, Map<PlayerId, Player> players) {
        players.forEach((id, player) -> player.receiveInfo(info));
    }
    
    private static void broadcastStateChange(GameState newState, Map<PlayerId, Player> players) {
        players.forEach((id, player) -> player.updateState(newState, newState.playerState(id)));
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
        Preconditions.checkArgument(players.size() == PlayerId.COUNT);
        Preconditions.checkArgument(playerNames.size() == PlayerId.COUNT);
        
        /* == Initialisation d'une partie de tCHu == */
        GameState state = GameState.initial(tickets, rng);
        
        players.forEach((playerId, player) -> player.initPlayers(playerId, playerNames));
    
        final Map<PlayerId, Info> infos = new EnumMap<>(PlayerId.class);
        players.forEach((id, player) -> infos.put(id, new Info(playerNames.get(id))));
        
        // Annonce du joueur qui jouera en premier
        broadcastInfo(infos.get(state.currentPlayerId()).willPlayFirst(), players);
    
        // Choix initial des billets
        for (Player player : players.values()) {
            player.setInitialTicketChoice(state.topTickets(Constants.INITIAL_TICKETS_COUNT));
            state = state.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
        }
        
        for (Map.Entry<PlayerId, Player> entry : players.entrySet()) {
            final PlayerId id = entry.getKey();
            final Player player = entry.getValue();
            
            broadcastStateChange(state, players);
            state = state.withInitiallyChosenTickets(id, player.chooseInitialTickets());
        }
        
        // Annonce des billets gardés par chaque joueur
        final GameState tempState = state;
        infos.forEach((playerId, info) -> broadcastInfo(
                info.keptTickets(tempState.playerState(playerId).ticketCount()),
                players
        ));
        
        /* == Logique d'une partie de tCHu == */
        boolean isPlaying = true;
        
        while (isPlaying) {
            final PlayerId currentPlayerId = state.currentPlayerId();
            final Player currentPlayer = players.get(currentPlayerId);
            final Info currentPlayerInfo = infos.get(currentPlayerId);
            
            broadcastStateChange(state, players);
            // Annonce du joueur qui joue ce tour
            broadcastInfo(currentPlayerInfo.canPlay(), players);
            
            switch (currentPlayer.nextTurn()) {
                case DRAW_TICKETS:
                    final SortedBag<Ticket> drawnTickets = state.topTickets(Constants.IN_GAME_TICKETS_COUNT);
                    final SortedBag<Ticket> chosenTickets = currentPlayer.chooseTickets(drawnTickets);
                    // Annonce des billets tirés par le joueur
                    broadcastInfo(currentPlayerInfo.drewTickets(Constants.IN_GAME_TICKETS_COUNT), players);
                    state = state.withChosenAdditionalTickets(drawnTickets, chosenTickets);
                    // Annonce des billets gardés par le joueur
                    broadcastInfo(currentPlayerInfo.keptTickets(chosenTickets.size()), players);
                    break;
                    
                case DRAW_CARDS:
                    for (int i = 0; i < IN_GAME_DRAW_CARDS_COUNT; ++i) {
                        state = state.withCardsDeckRecreatedIfNeeded(rng);
                        final int slot = currentPlayer.drawSlot();
                        
                        if (slot == Constants.DECK_SLOT) {
                            state = state.withBlindlyDrawnCard();
                            // Annonce de la pioche d'une carte face cachée
                            broadcastInfo(currentPlayerInfo.drewBlindCard(), players);
                        } else {
                            Card drawnCard = state.cardState().faceUpCard(slot);
                            state = state.withDrawnFaceUpCard(slot);
                            // Annonce de la pioche d'une carte face visible
                            broadcastInfo(currentPlayerInfo.drewVisibleCard(drawnCard), players);
                        }
                        
                        // Mise à jour de l'état sauf lors du dernier tirage,
                        // en vue d'un potentiel changement ultérieur (étape 12)
                        if (i < IN_GAME_DRAW_CARDS_COUNT - 1)
                            broadcastStateChange(state, players);
                    }
                    break;
                    
                case CLAIM_ROUTE:
                    final Route route = currentPlayer.claimedRoute();
                    final SortedBag<Card> initialCards = currentPlayer.initialClaimCards();
                    
                    if (route.level() == Route.Level.OVERGROUND) { // Route en surface
                        state = state.withClaimedRoute(route, initialCards);
                        // Annonce de la prise de possession de la route convoitée
                        broadcastInfo(currentPlayerInfo.claimedRoute(route, initialCards), players);
                    } else { // Route en tunnel
                        // Annonce de la tentative de prise de possession d'un tunnel
                        broadcastInfo(currentPlayerInfo.attemptsTunnelClaim(route, initialCards), players);
                        
                        final SortedBag.Builder<Card> drawnB = new SortedBag.Builder<>();
                        for (int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; ++i) {
                            state = state.withCardsDeckRecreatedIfNeeded(rng);
                            drawnB.add(state.topCard());
                            state = state.withoutTopCard();
                        }
                        final SortedBag<Card> drawnCards = drawnB.build();
    
                        final int addtitionalCardsCount = route.additionalClaimCardsCount(initialCards, drawnCards);
                        // Annonce de la pioche de cartes additionnelles
                        broadcastInfo(currentPlayerInfo.drewAdditionalCards(drawnCards, addtitionalCardsCount), players);
    
                        if (addtitionalCardsCount == 0) {
                            // Les cartes tirées n'impliquent aucune carte additionnelle
                            state = state.withClaimedRoute(route, initialCards);
                            broadcastInfo(currentPlayerInfo.claimedRoute(route, initialCards), players);
                        } else {
                            // Les cartes tirées impliquent au moins une carte additionnelle
                            final List<SortedBag<Card>> options = state
                                    .currentPlayerState()
                                    .possibleAdditionalCards(addtitionalCardsCount, initialCards, drawnCards);
    
                            if (options.isEmpty()) {
                                // Annonce de l'échec de prise de possession du tunnel
                                // TODO: enlever la duplication de la ligne
                                broadcastInfo(currentPlayerInfo.didNotClaimRoute(route), players);
                            } else if (addtitionalCardsCount > 0) {
                                final SortedBag<Card> chosenAdditional = currentPlayer.chooseAdditionalCards(options);
                                if (! chosenAdditional.isEmpty()) {
                                    final SortedBag<Card> totalCards = initialCards.union(chosenAdditional);
                                    state = state.withClaimedRoute(route, totalCards);
                                    // Annonce de la prise de possession du tunnel convoité
                                    broadcastInfo(currentPlayerInfo.claimedRoute(route, totalCards), players);
                                } else {
                                    // Annonce de l'échec de prise de possession du tunnel
                                    broadcastInfo(currentPlayerInfo.didNotClaimRoute(route), players);
                                }
                            }
                        }
    
                        state = state.withMoreDiscardedCards(drawnCards);
                    }
                    break;
            }
            
            if (state.lastTurnBegins())
                // Annonce du début du dernier tour
                broadcastInfo(currentPlayerInfo.lastTurnBegins(state.currentPlayerState().carCount()), players);
            
            if (currentPlayerId == state.lastPlayer())
                isPlaying = false;
            else
                state = state.forNextTurn();
        }
        
        /* == Fin d'une partie de tCHu == */
        broadcastStateChange(state, players);
        
        // TODO: génériser le calcul du bonus
        final Trail fstPlayerLongest = Trail.longest(state.playerState(PlayerId.PLAYER_1).routes());
        final Trail sndPlayerLongest = Trail.longest(state.playerState(PlayerId.PLAYER_2).routes());
        Trail longest;
        PlayerId bonusWinnerId;
        
        if (fstPlayerLongest.length() > sndPlayerLongest.length()) {
            longest = fstPlayerLongest;
            bonusWinnerId = PlayerId.PLAYER_1;
        } else if (sndPlayerLongest.length() > fstPlayerLongest.length()) {
            longest = sndPlayerLongest;
            bonusWinnerId = PlayerId.PLAYER_2;
        } else {
            bonusWinnerId = null;
            longest = null;
        }
        
        final Map<PlayerId, Integer> results = new EnumMap<>(PlayerId.class);
        for (PlayerId id : PlayerId.ALL)
            results.put(id, state.playerState(id).finalPoints());
        
        if (bonusWinnerId != null) {
            results.put(
                    bonusWinnerId,
                    results.get(bonusWinnerId) + Constants.LONGEST_TRAIL_BONUS_POINTS
            );
            // Annonce du vainqueur du bonus pour possession du plus long chemin
            broadcastInfo(infos.get(bonusWinnerId).getsLongestTrailBonus(longest), players);
        } else {
            // Égalité des plus longs chemins
            results.forEach((id, points) -> {
                results.put(id, points + Constants.LONGEST_TRAIL_BONUS_POINTS);
            });
            // Annonce des vainqueurs du bonus pour possession du plus long chemin (ex æqo)
            broadcastInfo(infos.get(PlayerId.PLAYER_1).getsLongestTrailBonus(fstPlayerLongest), players);
            broadcastInfo(infos.get(PlayerId.PLAYER_2).getsLongestTrailBonus(sndPlayerLongest), players);
        }

        final int offset = results.get(PlayerId.PLAYER_1).compareTo(results.get(PlayerId.PLAYER_2));
        if (offset == 0) {
            // Égalité
            broadcastInfo(Info.draw(List.copyOf(playerNames.values()), results.get(PlayerId.PLAYER_1)), players);
        } else {
            PlayerId winnerId = (offset < 0) ? PlayerId.PLAYER_2 : PlayerId.PLAYER_1;
            broadcastInfo(infos.get(winnerId).won(results.get(winnerId), results.get(winnerId.next())), players);
        }
    }
        
}
