package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.game.Constants.DECK_SLOT;

public final class TestClient {
    
    public static void main(String[] args) {
        System.out.println("Starting client!\n");
        
        RemotePlayerClient playerClient = new RemotePlayerClient(
                new TestPlayer(), "localhost", 5108
        );
        
        playerClient.run();
        
        System.out.println("Client done!");
    }
    
    private final static class TestPlayer implements Player {
        @Override
        public void initPlayers(PlayerId ownId,
                                Map<PlayerId, String> names) {
            System.out.println("Initialized players:");
            System.out.printf("\t- ownId: %s\n", ownId);
            System.out.printf("\t- playerNames: %s\n\n", names);
        }
    
        @Override
        public void receiveInfo(String info) {
            System.out.printf("Received info: %s\n\n", info);
        }
    
        @Override
        public void updateState(PublicGameState newState, PlayerState ownState) {
            System.out.println("States updated:");
            System.out.printf("\t- PublicGameState: %s\n", newState);
            System.out.printf("\t- PlayerState: %s\n\n", ownState);
        }
    
        @Override
        public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
            System.out.println("Set initial ticket choice:");
            System.out.printf("\t%s\n\n", tickets);
        }
    
        @Override
        public SortedBag<Ticket> chooseInitialTickets() {
            System.out.println("Chose initial tickets.\n");
            return SortedBag.of(ChMap.tickets().subList(15, 18));
        }
    
        @Override
        public TurnKind nextTurn() {
            System.out.println("Next turn : CLAIM_ROUTE\n");
            return TurnKind.CLAIM_ROUTE;
        }
    
        @Override
        public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
            return options;
        }
    
        @Override
        public int drawSlot() {
            System.out.println("Sent deck slot.\n");
            return DECK_SLOT;
        }
    
        @Override
        public Route claimedRoute() {
            return ChMap.routes().get(0);
        }
    
        @Override
        public SortedBag<Card> initialClaimCards() {
            return SortedBag.of(Card.RED);
        }
    
        @Override
        public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
            return SortedBag.of();
        }
    }
}