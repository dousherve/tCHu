package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameTest {
    
    private static final class TestPlayer implements Player {
        private static final int TURN_LIMIT = 1000;
        
        private final Random rng;
        private PlayerId id;
        private final List<Route> allRoutes;
        private final boolean verbose;
        
        private int turnCounter;
        private int infoCounter;
        private PlayerState ownState;
        private PublicGameState gameState;
        private SortedBag<Ticket> initialTicketChoice;
        private TurnKind currentTurnKind;
        
        private Route routeToClaim;
        private SortedBag initialClaimCards;
        
        public TestPlayer(long randomSeed, List<Route> allRoutes, boolean verbose) {
            this.rng = new Random(randomSeed);
            this.allRoutes = List.copyOf(allRoutes);
            this.turnCounter = 0;
            this.infoCounter = 0;
            this.verbose = verbose;
        }
        
        public TestPlayer(long randomSeed, List<Route> allRoutes) {
            this(randomSeed, allRoutes, false);
        }
        
        private void assertTurnKindIs(TurnKind turnKind) {
            if (currentTurnKind != turnKind)
                throw new Error(String.format("An action corresponding to turn kind %s has been attempted " +
                        "while current turn kind is %s.", turnKind, currentTurnKind));
        }
        
        @Override
        public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
            this.id = ownId;
            if (turnCounter != 0)
                throw new Error("The method should only be called at the beginning of the game.");
            if (!playerNames.containsKey(ownId))
                throw new Error("The player id must be a key in the player names map.");
        }
        
        @Override
        public void receiveInfo(String info) {
            if (verbose)
                System.out.println(info);
            ++infoCounter;
        }
        
        @Override
        public void updateState(PublicGameState newState, PlayerState ownState) {
            this.gameState = newState;
            this.ownState = ownState;
        }
        
        @Override
        public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
            if (turnCounter != 0)
                throw new Error("The method should only be called at the beginning of the game.");
            initialTicketChoice = SortedBag.of(tickets);
        }
        
        @Override
        public SortedBag<Ticket> chooseInitialTickets() {
            if (turnCounter != 0)
                throw new Error("The method should only be called at the beginning of the game.");
            
            var tempBag = initialTicketChoice
                    .difference(
                            SortedBag.of(initialTicketChoice.get(rng.nextInt(initialTicketChoice.size())))
                    );
            
            return tempBag.difference(SortedBag.of(tempBag.get(rng.nextInt(tempBag.size()))));
        }
        
        @Override
        public TurnKind nextTurn() {
            ++turnCounter;
            if (turnCounter > TURN_LIMIT)
                throw new Error("Abnormal number of turns played.");
    
            if (ownState.ticketCount() < 10) {
                currentTurnKind = TurnKind.DRAW_TICKETS;
                return TurnKind.DRAW_TICKETS;
            }
    
            List<Route> claimableRoutes = allRoutes.stream()
                    .filter(r -> ownState.canClaimRoute(r))
                    .filter(r -> ! ownState.routes().contains(r))
                    .filter(r -> ! gameState.playerState(id.next()).routes().contains(r))
                    .collect(Collectors.toList());
            
            if (claimableRoutes.stream().noneMatch(r -> r.level() == Route.Level.OVERGROUND) && gameState.canDrawCards()) {
                currentTurnKind = TurnKind.DRAW_CARDS;
                return TurnKind.DRAW_CARDS;
            } else {
                int routeIndex = rng.nextInt(claimableRoutes.size());
                Route route = claimableRoutes.get(routeIndex);
                List<SortedBag<Card>> cards = ownState.possibleClaimCards(route);
        
                routeToClaim = route;
                initialClaimCards = cards.get(rng.nextInt(cards.size()));
                currentTurnKind = TurnKind.CLAIM_ROUTE;
                return TurnKind.CLAIM_ROUTE;
            }
        }
        
        @Override
        public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
            assertTurnKindIs(TurnKind.DRAW_TICKETS);
            if (options.isEmpty() && gameState.canDrawTickets())
                throw new Error("Should have been able to draw tickets.");
            // Choisit 2 tickets parmi les 3 au hasard.
            return options.difference(SortedBag.of(options.get(rng.nextInt(options.size()))));
        }
        
        @Override
        public int drawSlot() {
            assertTurnKindIs(TurnKind.DRAW_CARDS);
            // Choisit un slot dans [-1; 4].
            return rng.nextInt(6) - 1;
        }
        
        @Override
        public Route claimedRoute() {
            assertTurnKindIs(TurnKind.CLAIM_ROUTE);
            if (routeToClaim == null)
                throw new Error("The method must not be called before a route has been attempted to be taken.");
            return routeToClaim;
        }
        
        @Override
        public SortedBag<Card> initialClaimCards() {
            assertTurnKindIs(TurnKind.CLAIM_ROUTE);
            if (initialClaimCards == null)
                throw new Error("The method must not be called before a route has been attempted to be taken.");
            return initialClaimCards;
        }
        
        @Override
        public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
            assertTurnKindIs(TurnKind.CLAIM_ROUTE);
            if (routeToClaim.level() != Route.Level.UNDERGROUND)
                throw new Error("The route to be claimed must be a tunnel.");
            // Picks a random option.
            return options.get(rng.nextInt(options.size()));
        }
        
        public int getTurnCounter() {
            return turnCounter;
        }
        
        public int getInfoCounter() {
            return infoCounter;
        }
        
        public PlayerState getState() {
            return ownState;
        }
    }
    
    @Test
    void play() {
        TestPlayer p1 = new TestPlayer(2021, ChMap.routes(), true);
        TestPlayer p2 = new TestPlayer(2021 + 2021, ChMap.routes());
    
        Game.play(
                Map.of(PlayerId.PLAYER_1, p1, PlayerId.PLAYER_2, p2),
                Map.of(PlayerId.PLAYER_1, "Joueur 1", PlayerId.PLAYER_2, "Joueur 2"),
                SortedBag.of(ChMap.tickets()), new Random()
        );
    
        assertTrue(p1.getInfoCounter() >= p1.getTurnCounter());
        assertTrue(p1.getState().ticketCount() >= 10);
        assertTrue(p1.getState().routes().size() > 0);
        assertTrue(p1.getState().carCount() <= 2 || p2.getState().carCount() <= 2);
    }
    
    @Test
    void playFailsOnBadArguments() {
        Map<PlayerId, Player> badMap1 = Map.of();
        Map<PlayerId, Player> goodMap1 = Map.of(PlayerId.PLAYER_1, new TestPlayer(1, List.of()), PlayerId.PLAYER_2, new TestPlayer(2, List.of()));
        Map<PlayerId, Player> badMap2 = Map.of(PlayerId.PLAYER_1, new TestPlayer(1, List.of()));
        Map<PlayerId, String> badMap3 = Map.of();
        Map<PlayerId, String> goodMap2 = Map.of(PlayerId.PLAYER_1, "P1", PlayerId.PLAYER_2, "P2");
        Map<PlayerId, String> badMap4 = Map.of(PlayerId.PLAYER_2, "P2");
        
        assertThrows(IllegalArgumentException.class, () -> Game.play(badMap1, goodMap2, SortedBag.of(), TestRandomizer.newRandom()));
        assertThrows(IllegalArgumentException.class, () -> Game.play(badMap2, goodMap2, SortedBag.of(), TestRandomizer.newRandom()));
        assertThrows(IllegalArgumentException.class, () -> Game.play(goodMap1, badMap3, SortedBag.of(), TestRandomizer.newRandom()));
        assertThrows(IllegalArgumentException.class, () -> Game.play(goodMap1, badMap4, SortedBag.of(), TestRandomizer.newRandom()));
    }
    
}